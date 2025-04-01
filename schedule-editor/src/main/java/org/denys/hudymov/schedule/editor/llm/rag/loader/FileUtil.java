package org.denys.hudymov.schedule.editor.llm.rag.loader;

import java.nio.file.Paths;

public class FileUtil {
    private FileUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String getJarDirectory() {
        return Paths.get("").toAbsolutePath().toString();
    }
}
