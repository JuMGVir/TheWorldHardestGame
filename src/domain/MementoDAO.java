package domain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * MementoDAO — implementación de MementoManager para persistencia en texto plano (.txt).
 * 
 * Implementa MementoManager utilizando un formato de text donde cada entidad.
 * 
 * Es el equivalente al "Caretaker" del patrón Memento: guarda y recupera
 * objetos Memento en formato .txt, permitiendo exportar/importar partidas
 * en un archivo que puede ser leído y editado por un ser humano.
 * 
 */
public class MementoDAO implements MementoManager {

    // Registro de parseadores (PARSERS)
    // Mapa que asocia nombres de clase (en el archivo .txt) con funciones
    // que parsean sus datos y los acumulan en un ImportContext.
	//En otras palabras el lee la estrucutra smenaticad de la sentencia txt, y ubica el ecnabezado de cada linea 
    // STRATEGY_PARSERS
    // Mapa que asocia nombres de estrategias de movimiento con funciones
    // que reconstruyen el objeto MovementStrategy correspondiente. 
    private static final Map<String, BiConsumer<String, ImportContext>> PARSERS = new HashMap<>();
    private static final Map<String, Function<String, MovementStrategy>> STRATEGY_PARSERS = new HashMap<>();

    // Convierte nombres antiguos (RED, BLUE, GREEN) a los nuevos (BLINKY, INKY, CLYDE) para backward compatibility
    private static PlayerType parsePlayerType(String name) {
        return switch (name) {
            case "RED" -> PlayerType.BLINKY;
            case "BLUE" -> PlayerType.INKY;
            case "GREEN" -> PlayerType.CLYDE;
            default -> PlayerType.valueOf(name);
        };
    }

    static {
        // Parseador para movimiento lineal: divide velocidad entre multiplicador para obtener dx,dy originales
        STRATEGY_PARSERS.put("LinearMovement", data -> {
            String[] sp = data.split(","); // Separa los valores vx, vy, mult
            double mult = Double.parseDouble(sp[2]); // Obtiene el multiplicador de velocidad
            return new LinearMovement( // Reconstruye el movimiento lineal
                Double.parseDouble(sp[0]) / mult, // dx = vx / mult
                Double.parseDouble(sp[1]) / mult, mult); // dy = vy / mult
        });
        // Parseador para movimiento circular: extrae centro, radio y velocidad angular
        STRATEGY_PARSERS.put("CircularMovement", data -> {	
            String[] sp = data.split(","); // Separa cx, cy, radio, vel
            return new CircularMovement( // Reconstruye el movimiento circular
                Double.parseDouble(sp[0]), Double.parseDouble(sp[1]), // Centro (cx, cy)
                Double.parseDouble(sp[2]), Double.parseDouble(sp[3])); // Radio y velocidad
        });

        // Parseador para jugador: primera línea crea P1, segunda línea crea P2 (mismo prefijo "Player:")
        // Formato: Player:x,y,TIPO,rx,ry,escudos
        PARSERS.put("Player", (data, ctx) -> {
            String[] p = data.split(","); // Separa x, y, tipo, rx, ry, escudos
            Player nuevo = new Player( // Crea el jugador con posición y tipo
                Double.parseDouble(p[0]), Double.parseDouble(p[1]),
                parsePlayerType(p[2]));
            nuevo.setRespawnPoint(Double.parseDouble(p[3]), Double.parseDouble(p[4])); // Asigna punto de respawn
            if (p.length > 5) nuevo.setShieldCharges(Integer.parseInt(p[5])); // Restaura escudos
            if (ctx.playerCount == 0) {
                ctx.player = nuevo; // Primera línea: jugador 1
            } else {
                ctx.player2 = nuevo; // Segunda línea: jugador 2
            }
            ctx.playerCount++; // Incrementa el contador de jugadores
        });
        // Parseador para muertes: primera línea P1, segunda línea P2
        PARSERS.put("Deaths", (data, ctx) -> {
            if (ctx.deathsCount == 0) {
                ctx.deaths = Integer.parseInt(data); // Primera línea: muertes de P1
            } else {
                ctx.deaths2 = Integer.parseInt(data); // Segunda línea: muertes de P2
            }
            ctx.deathsCount++; // Incrementa el contador de líneas Deaths
        });
        // Parseador para puntuación: primera línea P1, segunda línea P2
        PARSERS.put("Score", (data, ctx) -> {
            if (ctx.scoreCount == 0) {
                ctx.score = Integer.parseInt(data); // Primera línea: puntuación de P1
            } else {
                ctx.score2 = Integer.parseInt(data); // Segunda línea: puntuación de P2
            }
            ctx.scoreCount++; // Incrementa el contador de líneas Score
        });
        // Parseador para el tiempo restante del nivel en frames
        PARSERS.put("Time", (data, ctx) -> ctx.levelTime = Integer.parseInt(data));
        // Parseador para datos del jugador 2 (formato antiguo "Player2:")
        PARSERS.put("Player2", (data, ctx) -> {
            String[] p = data.split(","); // Separa x, y, tipo, rx, ry, escudos
            ctx.player2 = new Player( 
                Double.parseDouble(p[0]), Double.parseDouble(p[1]),
                parsePlayerType(p[2]));
            ctx.player2.setRespawnPoint(Double.parseDouble(p[3]), Double.parseDouble(p[4])); // Asigna respawn
            if (p.length > 5) ctx.player2.setShieldCharges(Integer.parseInt(p[5])); // Restaura escudos
        });
      
        PARSERS.put("Deaths2", (data, ctx) -> ctx.deaths2 = Integer.parseInt(data));

        PARSERS.put("Score2", (data, ctx) -> ctx.score2 = Integer.parseInt(data));

        PARSERS.put("Winner", (data, ctx) -> ctx.winner = Integer.parseInt(data));

        PARSERS.put("GameOver", (data, ctx) -> ctx.gameOver = data.equals("1"));
        // Parseador para checkpoint: guarda posición guardada en zona intermedia
        PARSERS.put("Checkpoint", (data, ctx) -> {
            String[] p = data.split(","); // Separa x, y
            ctx.checkpointX = Double.parseDouble(p[0]); // Coordenada X
            ctx.checkpointY = Double.parseDouble(p[1]); // Coordenada Y
            ctx.hasCheckpoint = true; // Marca que existe un checkpoint
        });
        // Parseador para zonas seguras: tipo, posición, tamaño y punto de respawn
        PARSERS.put("SafeZone", (data, ctx) -> {
            String[] p = data.split(","); // Separa tipo, x, y, ancho, alto, respawnX, respawnY
            SafeZone zone = switch (p[0]) {
                case "START" -> new StartSafeZone(
                    Double.parseDouble(p[1]), Double.parseDouble(p[2]),
                    Double.parseDouble(p[3]), Double.parseDouble(p[4]),
                    Double.parseDouble(p[5]), Double.parseDouble(p[6]));
                case "INTERMEDIATE" -> new IntermediateSafeZone(
                    Double.parseDouble(p[1]), Double.parseDouble(p[2]),
                    Double.parseDouble(p[3]), Double.parseDouble(p[4]),
                    Double.parseDouble(p[5]), Double.parseDouble(p[6]));
                case "FINAL" -> new FinalSafeZone(
                    Double.parseDouble(p[1]), Double.parseDouble(p[2]),
                    Double.parseDouble(p[3]), Double.parseDouble(p[4]),
                    Double.parseDouble(p[5]), Double.parseDouble(p[6]));
                default -> throw new IllegalArgumentException("Unknown SafeZone type: " + p[0]);
            };
            ctx.safeZones.add(zone);
        });
        // Parseador para paredes: posición y tamaño
        PARSERS.put("Wall", (data, ctx) -> {
            String[] p = data.split(","); // Separa x, y, ancho, alto
            ctx.walls.add(new Wall( // Crea y añade la pared
                Double.parseDouble(p[0]), Double.parseDouble(p[1]), // Posición x, y
                Double.parseDouble(p[2]), Double.parseDouble(p[3]))); // Ancho, alto
        });

        // Parseador para monedas: formato x,y,collected (3 campos) o x,y,skinType,collected (4 campos, antiguo)
        PARSERS.put("Coin", (data, ctx) -> {
            String[] p = data.split(","); // Separa posicion, skin (opcional) y estado
            if (p.length >= 4) { // Formato antiguo con 4 campos: x, y, skinType, collected
                SkinCoin c = new SkinCoin(Double.parseDouble(p[0]), Double.parseDouble(p[1]),
                    parsePlayerType(p[2])); // Crea moneda con skin
                if (p.length > 3) c.setCollected(p[3].equals("1")); // Marca como recogida si es 1
                ctx.coins.add(c);
            } else { // Formato actual: 3 campos (x, y, collected)
                Coin c = new Coin(Double.parseDouble(p[0]), Double.parseDouble(p[1])); // Crea moneda normal
                if (p.length > 2) c.setCollected(p[2].equals("1")); // Marca como recogida si es 1
                ctx.coins.add(c);
            }
        });
        // Parseador para SkinCoin: formato x,y,skinType,collected (4 campos)
        PARSERS.put("SkinCoin", (data, ctx) -> {
            String[] p = data.split(","); // Separa posicion, skin y estado
            SkinCoin c = new SkinCoin(Double.parseDouble(p[0]), Double.parseDouble(p[1]),
                parsePlayerType(p[2])); // Crea moneda con skin
            if (p.length > 3) c.setCollected(p[3].equals("1")); // Marca como recogida si es 1
            ctx.coins.add(c);
        });
        // Parseador para enemigos: posición, tamaño, vida y estrategia de movimiento
        PARSERS.put("Enemy", (data, ctx) -> {
            String[] p = data.split(",", 6); // Divide en máximo 6 partes (campos + estrategia)
            if (p.length < 6) return; // Si faltan datos, ignora este enemigo
            boolean alive = p[4].equals("1"); // Determina si el enemigo está vivo
            int sc = p[5].indexOf(':'); // Busca el separador nombre:datos de estrategia
            if (sc < 0) return; // Si no tiene estrategia, ignora
            String stratName = p[5].substring(0, sc); // Nombre de la estrategia
            String stratData = p[5].substring(sc + 1); // Datos de la estrategia
            Function<String, MovementStrategy> stratParser = STRATEGY_PARSERS.get(stratName); // Busca el parseador
            if (stratParser == null) return; // Si no reconoce la estrategia, ignora
            MovementStrategy strat = stratParser.apply(stratData); // Reconstruye la estrategia
            Enemy e = new Enemy( // Crea el enemigo con su estrategia
                Double.parseDouble(p[0]), Double.parseDouble(p[1]), // Posición
                Double.parseDouble(p[2]), Double.parseDouble(p[3]), // Tamaño
                strat);
            e.setAlive(alive); // Restaura si estaba vivo o muerto
            ctx.enemies.add(e); // Añade el enemigo a la lista
        });
        // Parseador para bombas: posición y estado (explotada=1, no=0)
        PARSERS.put("Bomb", (data, ctx) -> {
            String[] p = data.split(","); // Separa x, y y opcionalmente exploded
            Bomb b = new Bomb(Double.parseDouble(p[0]), Double.parseDouble(p[1])); // Crea la bomba
            if (p.length > 2) b.setExploded(p[2].equals("1")); // Marca como explotada si es 1
            ctx.bombs.add(b); // Añade la bomba a la lista
        });
        // Parseador para vidas extra: posición y estado (recogida=1, no=0)
        PARSERS.put("Life", (data, ctx) -> {
            String[] p = data.split(","); // Separa x, y y opcionalmente collected
            Life l = new Life(Double.parseDouble(p[0]), Double.parseDouble(p[1])); // Crea la vida extra
            if (p.length > 2) l.setCollected(p[2].equals("1")); // Marca como recogida si es 1
            ctx.lives.add(l); // Añade la vida extra a la lista
        });

    }

    // saveGame — Exporta un Memento a archivo .txt

    /**
     * Guarda el estado completo de una partida (Memento) en un archivo de texto.
     * Escribe cada entidad en una línea con el formato Clase:datos.
     * 
     * @param data El Memento con el estado de la partida a exportar
     * @param file El archivo .txt de destino
     */
    @Override
    public void saveGame(Memento data, File file) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) { // Abre el archivo para escritura
            bw.write("// FORMATO: NombreClase:datos_componente"); bw.newLine(); // Comentario de cabecera
            bw.newLine(); // Línea en blanco

            int nivel = data.getCurrentLevel(); // Obtiene el número de nivel
            bw.write("=== NIVL:" + nivel + " ==="); bw.newLine(); // Marca de inicio de nivel

            // Jugador 1
            Player p1 = data.getPlayer1(); // Obtiene el jugador 1 del Memento
            if (p1 != null) {
                bw.write("Player:" + p1.x + "," + p1.y + "," + p1.getPlayerType() // Escribe posición y tipo
                    + "," + p1.getRespawnX() + "," + p1.getRespawnY() // Escribe punto de respawn
                    + "," + p1.getShieldCharges()); bw.newLine(); // Escribe escudos acumulados
            }
            bw.write("Deaths:" + data.getDeaths1()); bw.newLine(); // Escribe muertes de P1
            bw.write("Score:" + data.getScore1()); bw.newLine(); // Escribe puntuación de P1

            // Jugador 2 (solo PvP)
            Player p2 = data.getPlayer2(); // Obtiene el jugador 2 del Memento
            if (p2 != null) {
                bw.write("Player:" + p2.x + "," + p2.y + "," + p2.getPlayerType() // Escribe posición y tipo
                    + "," + p2.getRespawnX() + "," + p2.getRespawnY() // Escribe punto de respawn
                    + "," + p2.getShieldCharges()); bw.newLine(); // Escribe escudos
                bw.write("Deaths:" + data.getDeaths2()); bw.newLine(); // Escribe muertes de P2
                bw.write("Score:" + data.getScore2()); bw.newLine(); // Escribe puntuación de P2
                bw.write("Winner:" + data.getWinner()); bw.newLine(); // Escribe ganador
                bw.write("GameOver:" + (data.isGameOver() ? 1 : 0)); bw.newLine(); // Escribe estado de tiempo
            }

            bw.write("Time:" + data.getLevelTimeRemaining()); bw.newLine(); // Escribe tiempo restante

            // Checkpoint
            if (data.hasCheckpoint()) {
                bw.write("Checkpoint:" + data.getCheckpointX() + "," + data.getCheckpointY()); bw.newLine();
            } else {
                bw.write("Checkpoint:0.0,0.0"); bw.newLine(); // Sin checkpoint: escribe 0,0
            }

            // Entidades del nivel
            Level level = data.getLevel(); // Obtiene el nivel del Memento
            if (level != null) {
                for (SafeZone sz : level.getSafeZones()) { // Itera sobre zonas seguras
                    bw.write("SafeZone:" + sz.getTypeName() + "," + sz.x + "," + sz.y // Escribe tipo y posición
                        + "," + sz.width + "," + sz.height // Escribe tamaño
                        + "," + sz.getRespawnX() + "," + sz.getRespawnY()); bw.newLine(); // Escribe respawn
                }
                for (Wall w : level.getWalls()) { // Itera sobre paredes
                    bw.write("Wall:" + w.x + "," + w.y + "," + w.width + "," + w.height); bw.newLine(); // Escribe posición y tamaño
                }
                for (Coin c : level.getCoins()) { // Itera sobre monedas (Coin, SkinCoin o Life)
                    PlayerType skin = c.getSkinType();
                    if (skin != null) {
                        bw.write("SkinCoin:" + c.x + "," + c.y + "," + skin
                            + "," + (c.isCollected() ? 1 : 0));
                    } else if (c.isLife()) {
                        bw.write("Life:" + c.x + "," + c.y + "," + (c.isCollected() ? 1 : 0));
                    } else {
                        bw.write("Coin:" + c.x + "," + c.y + "," + (c.isCollected() ? 1 : 0));
                    }
                    bw.newLine();
                }
                for (Enemy en : level.getEnemies()) { // Itera sobre enemigos
                    bw.write("Enemy:" + en.x + "," + en.y + "," + en.width + "," + en.height // Escribe posición y tamaño
                        + "," + (en.isAlive() ? 1 : 0) + ","); // Escribe si está vivo
                    MovementStrategy strat = en.getStrategy();
                    bw.write(strat.toExportString());
                    bw.newLine();
                }
                for (Bomb b : level.getBombs()) { // Itera sobre bombas
                    bw.write("Bomb:" + b.x + "," + b.y + "," + (b.isExploded() ? 1 : 0)); bw.newLine(); // Escribe posición y estado
                }

            }

            bw.write("=== FIN NIVL:" + nivel + " ==="); bw.newLine(); // Marca de fin de nivel
        }
    }

    // loadGame — Importa un Memento desde archivo .txt

    /**
     * Carga el estado completo de una partida desde un archivo de texto.
     * Lee línea por línea, identifica el tipo de cada entidad por el prefijo
     * antes de los dos puntos, y usa el PARSERS correspondiente para reconstruirla.
     * 
     * @param file El archivo .txt a importar
     * @return Memento con el estado restaurado de la partida
     */
    @Override
    public Memento loadGame(File file) throws IOException {
        ImportContext ctx = new ImportContext(); // Contexto temporal para acumular datos parseados
        int levelNum = 1; // Nivel por defecto si no se especifica
        boolean inLevel = false; // Indica si estamos dentro de un bloque === NIVL#: ===

        try (BufferedReader br = new BufferedReader(new FileReader(file))) { // Abre el archivo para lectura
            String line; // Almacena cada línea leída
            while ((line = br.readLine()) != null) { // Lee línea por línea
                line = line.trim(); // Elimina espacios al inicio y final
                if (line.isEmpty() || line.startsWith("//")) continue; // Salta líneas vacías o comentarios

                if (line.startsWith("=== ")) { // Detecta delimitadores de nivel
                    if (line.startsWith("=== NIVL:")) { // Inicio de nivel
                        levelNum = Integer.parseInt(line.replace("=== NIVL:", "").replace("===", "").trim()); // Extrae número
                        inLevel = true; 
                    } else if (line.startsWith("=== FIN NIVL:")) {
                        inLevel = false; // Marca que salimos del nivel
                    }
                    continue;
                    }
                if (!inLevel) continue; // Salta líneas fuera de un bloque de nivel

                int colonIdx = line.indexOf(':'); // Busca el separador nombre:dato
                if (colonIdx < 0) continue; // Si no tiene formato válido, salta
                String className = line.substring(0, colonIdx);
                String data = line.substring(colonIdx + 1);

                BiConsumer<String, ImportContext> parser = PARSERS.get(className);	
                if (parser != null) {
                    parser.accept(data, ctx); // Ejecuta el parseador con los datos
                }
            }
        }

        // Construye el Memento con todos los datos parseados
        Memento result = new Memento(); // Crea un nuevo Memento vacío
        result.setPvP(ctx.player2 != null); // Detecta si es PvP por la presencia de P2
        result.setCurrentLevel(levelNum); // Asigna el nivel importado
        result.setDeaths1(ctx.deaths); // Restaura muertes de P1
        result.setDeaths2(ctx.deaths2); // Restaura muertes de P2
        result.setScore1(ctx.score); // Restaura puntuación de P1
        result.setScore2(ctx.score2); // Restaura puntuación de P2
        result.setGameOver(ctx.gameOver); // Restaura si el tiempo se agotó
        result.setWinner(ctx.winner); // Restaura ganador

        // Crea el nivel a partir de las listas parseadas
        Level level = LevelFactory.createLevelFromImport(ctx.safeZones, ctx.walls, ctx.coins,
            ctx.enemies, ctx.bombs, ctx.lives, 60); // Construye el nivel con fábrica
        result.setLevel(level); // Asigna el nivel al Memento
        result.setLevelTimeRemaining((ctx.levelTime > 0) ? ctx.levelTime : level.getTimeLimit() * 60); // Tiempo restante

        // Restaura el jugador 1
        if (ctx.player != null) {
            result.setPlayer1(ctx.player); // Usa el jugador importado si existe
        } else {
            for (SafeZone sz : ctx.safeZones) { // Busca la zona de inicio
                if (sz.isStart()) { // Si encuentra una zona START
                    Player defaultP1 = new Player(sz.getRespawnX(), sz.getRespawnY()); // Crea jugador por defecto
                    defaultP1.setRespawnPoint(sz.getRespawnX(), sz.getRespawnY()); // Asigna su respawn
                    result.setPlayer1(defaultP1); // Asigna el jugador al Memento
                    break;
                }
            }
        }
        if (result.isPvP()) {
            result.setPlayer2(ctx.player2); // Restaura jugador 2 en modo PvP
        }

        // Restaura el checkpoint si existe
        if (ctx.hasCheckpoint && ctx.checkpointX != 0 && ctx.checkpointY != 0) {
            result.setCheckpoint(ctx.checkpointX, ctx.checkpointY); // Guarda la posición del checkpoint
        }

        return result; // Devuelve el Memento con el estado restaurado
    }
}
