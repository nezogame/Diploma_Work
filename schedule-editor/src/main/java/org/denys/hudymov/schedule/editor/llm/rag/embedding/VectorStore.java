package org.denys.hudymov.schedule.editor.llm.rag.embedding;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.annotation.PostConstruct;
import java.net.URISyntaxException;
import java.util.List;
import org.denys.hudymov.schedule.editor.llm.rag.loader.DocumentLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component

public class VectorStore {
    private static final Logger log = LoggerFactory.getLogger(VectorStore.class);
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final DocumentLoader documentLoader;

    public VectorStore(EmbeddingStore<TextSegment> embeddingStore, DocumentLoader documentLoader) {
        this.embeddingStore = embeddingStore;
        this.documentLoader = documentLoader;
    }

    @PostConstruct
    public void ingestDocuments() {
        List<Document> documents = null;
        try {
            documents = documentLoader.loadDocuments();
        } catch (URISyntaxException e) {
            log.error("string could not be parsed as a URI reference", e);
        }
        EmbeddingStoreIngestor.ingest(documents, embeddingStore);
    }
}
