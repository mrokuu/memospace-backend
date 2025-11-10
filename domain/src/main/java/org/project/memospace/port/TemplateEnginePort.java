package org.project.memospace.domain.port;

import org.project.memospace.domain.model.RenderedClozeCard;

import java.util.List;
import java.util.Map;

public interface TemplateEnginePort {

    /**
     * Renders normal (non-cloze) templates
     */
    RenderedCard renderNormal(String frontTemplate, String backTemplate, Map<String, String> fieldValues);

    /**
     * Renders cloze templates, returning one card per unique cloze index
     */
    List<RenderedClozeCard> renderCloze(String frontTemplate, String backTemplate, Map<String, String> fieldValues);

    class RenderedCard {
        private final String front;
        private final String back;

        public RenderedCard(String front, String back) {
            this.front = front;
            this.back = back;
        }

        public String getFront() {
            return front;
        }

        public String getBack() {
            return back;
        }
    }
}