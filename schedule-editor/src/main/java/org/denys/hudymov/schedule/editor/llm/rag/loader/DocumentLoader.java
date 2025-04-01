package org.denys.hudymov.schedule.editor.llm.rag.loader;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DocumentLoader {
    @Value("${rag.folder}")
    private String ragFolderName;

    public List<Document> loadDocuments() throws URISyntaxException {
        URL resourceUrl = DocumentLoader.class.getClassLoader().getResource(ragFolderName);
        var ragFolder = Path.of(resourceUrl.toURI());
        return FileSystemDocumentLoader.loadDocuments(ragFolder);
    }
}