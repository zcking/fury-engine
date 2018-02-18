package com.zcking.furyengine.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ResourceUtils {

    public static String loadResource(String filePath) throws Exception {
        String result;
        try (InputStream in = Class.forName(ResourceUtils.class.getName()).getResourceAsStream(filePath);
            Scanner scanner = new Scanner(in, "UTF-8")) {

            result = scanner.useDelimiter("\\A").next();
        }
        return result;
    }

    public static StringBuilder loadResourceBuffer(String file) {
        StringBuilder contents = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                contents.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Could not read file!");
            e.printStackTrace();
            System.exit(-1);
        }

        return contents;
    }

    public static List<String> readLines(String fileName) throws Exception {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Class.forName(
                ResourceUtils.class.getName()
        ).getResourceAsStream(fileName)))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }

        return lines;
    }

}
