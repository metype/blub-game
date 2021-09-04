package com.metype.game;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LevelParser {

    Map<String, Object> prop = new HashMap();
    String levelName;
    File file;

    public LevelParser(File levelFile) throws IllegalArgumentException, IOException {
        if(!levelFile.exists()) throw new IllegalArgumentException("File provided must exist.");
        if(!levelFile.isFile()) throw new IllegalArgumentException("File provided must not be a directory.");
        if(!levelFile.canRead()) throw new IllegalArgumentException("File provided cannot be read.");
        BufferedReader br = new BufferedReader(new FileReader(levelFile));
        levelName = levelFile.getName().split("\\.lev")[0];
        file = levelFile;
        String s;
        while ((s = br.readLine()) != null) {
            if (s.split("(?!\\B\"[^\"]*)=(?![^\"]*\"\\B)").length < 2) continue;
            String attrib = s.split("(?!\\B\"[^\"]*)=(?![^\"]*\"\\B)")[0];
            String value = s.split("(?!\\B\"[^\"]*)=(?![^\"]*\"\\B)")[1];
            if(value.startsWith("\"") && value.endsWith("\"")){
                Pattern pattern = Pattern.compile("(?<=\")(.)*(?=\")");
                Matcher matcher = pattern.matcher(value);
                if (matcher.find()) {
                    prop.put(attrib, matcher.group(0));
                }
            }
            if(value.startsWith("[") && value.endsWith("]") && !attrib.equalsIgnoreCase("levelData")){
                Pattern pattern = Pattern.compile("\\[.*\\]");
                Matcher matcher = pattern.matcher(value);
                if (matcher.find()) {
                    prop.put(attrib, matcher.group(0).split("[\\[,\\]]"));
                }
            }
        }
    }

    public void loadLevel(File levelFile) throws IOException {
        if(!levelFile.exists()) throw new IllegalArgumentException("File provided must exist.");
        if(!levelFile.isFile()) throw new IllegalArgumentException("File provided must not be a directory.");
        if(!levelFile.canRead()) throw new IllegalArgumentException("File provided cannot be read.");
        BufferedReader br = new BufferedReader(new FileReader(levelFile));
        levelName = levelFile.getName().split("\\.lev")[0];
        file = levelFile;
        String s;
        while ((s = br.readLine()) != null) {
            if (s.split("(?!\\B\"[^\"]*)=(?![^\"]*\"\\B)").length < 2) continue;
            String attrib = s.split("(?!\\B\"[^\"]*)=(?![^\"]*\"\\B)")[0];
            String value = s.split("(?!\\B\"[^\"]*)=(?![^\"]*\"\\B)")[1];
            if(attrib.equals("levelData")) {
                Pattern pattern = Pattern.compile("\\[.*]");
                Matcher matcher = pattern.matcher(value);
                if (matcher.find()) {
                    String[] data = matcher.group(0).split("(?!\\B\"[^\"]*),(?![^\"]*\"\\B)");
                    Level l = new Level(Integer.parseInt(data[0].replace("[", "")), Integer.parseInt(data[1]));
                    for (int i = 0; i < l.t.length; i++) {
                        for (int j = 0; j < l.t[i].length; j++) {
                            try {
                                if (data[l.t.length * j + i + 2].contains("{")) {
                                    Tile tile = new Tile(Integer.parseInt(data[l.t.length * j + i + 2].split("\\{")[0]));
                                    String[] meta = data[l.t.length * j + i + 2].split("\\{")[1].split("\\}")[0].split("(?!\\B\"[^\"]*);(?![^\"]*\"\\B)");
                                    for (String metaData : meta) {
                                        Field modified = tile.getClass().getField(metaData.split(":")[0]);
                                        if (int.class.equals(modified.getType())) {
                                            modified.setInt(tile, Integer.parseInt(metaData.split(":")[1]));
                                        }
                                        if (String.class.equals(modified.getType())) {
                                            modified.set(tile, metaData.split(":")[1].split("\"")[1]);
                                        }
                                        if (Vector[].class.equals(modified.getType())) {
                                            String[] entries = metaData.split(":")[1].split(" ");
                                            Vector[] newArr = new Vector[entries.length/2];
                                            for(int k=0;k<entries.length;k+=2){
                                                newArr[k/2] = new Vector(Double.parseDouble(entries[k]),Double.parseDouble(entries[k+1]));
                                            }
                                            modified.set(tile, newArr);
                                        }
                                    }
                                    l.t[i][j] = tile;
                                } else {
                                    l.t[i][j] = new Tile(Integer.parseInt(data[l.t.length * j + i + 2].split("[\\]]")[0]));
                                }
                            } catch (ArrayIndexOutOfBoundsException e) {
                                l.t[i][j] = new Tile(0);
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    prop.put(attrib, l);
                }
            }
        }
    }

    public Object get(String key){
        return prop.get(key);
    }

    public String levelName(){ return this.levelName; }

    public File file() { return this.file; }
}
