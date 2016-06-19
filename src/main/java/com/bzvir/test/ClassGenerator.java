package com.bzvir.test;

/**
 * Created by bohdan on 19.06.16.
 */
public class ClassGenerator {

    public static String fixClassDeclaration(String textLine) {
        String[] lines = textLine.split(System.getProperty("line.separator"));
        String packageName = extractPackageName(lines[0]);

        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(packageName).append(";\n");

        for (int i = 1; i < lines.length; i++) {

            if (lines[i].startsWith("////")) {
            } else if (lines[i].startsWith("class")) {
                sb.append("@lombok.Getter\n@lombok.Setter\n@lombok.ToString\n");
                sb.append(lines[i].replaceFirst(packageName + ".", ""));
            } else {
                sb.append(lines[i]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static String extractPackageName(String line) {
        String packageLine = line.split(" ")[1];
        return packageLine.substring(0, packageLine.lastIndexOf("."));
    }
}
