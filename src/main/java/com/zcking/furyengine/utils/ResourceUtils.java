package com.zcking.furyengine.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
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

}
