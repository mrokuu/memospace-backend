package org.memospace.template;

import org.memospace.model.RenderedClozeCard;
import org.memospace.port.TemplateEnginePort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TemplateEngineAdapter implements TemplateEnginePort {

    private static final Pattern FIELD_PATTERN = Pattern.compile("\\{\\{([^}]+)\\}\\}");
    private static final Pattern CLOZE_PATTERN = Pattern.compile("\\{\\{c(\\d+)::([^:}]+)(?:::([^}]+))?\\}\\}");

    @Override
    public RenderedCard renderNormal(String frontTemplate, String backTemplate, Map<String, String> fieldValues) {
        String front = renderTemplate(frontTemplate, fieldValues);
        String back = renderTemplate(backTemplate, fieldValues);
        return new RenderedCard(front, back);
    }

    @Override
    public List<RenderedClozeCard> renderCloze(String frontTemplate, String backTemplate, Map<String, String> fieldValues) {
        List<RenderedClozeCard> cards = new ArrayList<>();
        Set<Integer> clozeIndices = getAllClozeIndices(frontTemplate, backTemplate, fieldValues);

        for (Integer clozeIndex : clozeIndices) {
            String front = renderClozeTemplate(frontTemplate, fieldValues, clozeIndex, true);
            String back = renderClozeTemplate(backTemplate, fieldValues, clozeIndex, false);
            cards.add(new RenderedClozeCard(clozeIndex, front, back));
        }

        return cards;
    }

    private String renderTemplate(String template, Map<String, String> fieldValues) {
        String result = template;

        // Replace {{FieldName}} with field values
        Matcher matcher = FIELD_PATTERN.matcher(template);
        while (matcher.find()) {
            String fieldReference = matcher.group(1);
            String replacement;

            if (fieldReference.startsWith("cloze:")) {
                String fieldName = fieldReference.substring(6);
                String fieldValue = fieldValues.getOrDefault(fieldName, "");
                replacement = renderAllClozeAsText(fieldValue);
            } else {
                replacement = fieldValues.getOrDefault(fieldReference, "");
            }

            result = result.replace("{{" + fieldReference + "}}", replacement);
        }

        return result;
    }

    private String renderClozeTemplate(String template, Map<String, String> fieldValues, int targetClozeIndex, boolean isForFront) {
        String result = template;

        // Replace {{FieldName}} with field values
        Matcher matcher = FIELD_PATTERN.matcher(template);
        while (matcher.find()) {
            String fieldReference = matcher.group(1);
            String replacement;

            if (fieldReference.startsWith("cloze:")) {
                String fieldName = fieldReference.substring(6);
                String fieldValue = fieldValues.getOrDefault(fieldName, "");
                replacement = renderClozeField(fieldValue, targetClozeIndex, isForFront);
            } else {
                replacement = fieldValues.getOrDefault(fieldReference, "");
            }

            result = result.replace("{{" + fieldReference + "}}", replacement);
        }

        return result;
    }

    private String renderClozeField(String fieldValue, int targetClozeIndex, boolean isForFront) {
        Matcher matcher = CLOZE_PATTERN.matcher(fieldValue);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            int clozeIndex = Integer.parseInt(matcher.group(1));
            String clozeText = matcher.group(2);
            String hint = matcher.group(3);

            String replacement;
            if (clozeIndex == targetClozeIndex) {
                if (isForFront) {
                    // Replace with blank on front
                    String blankText = hint != null ? "[...] (" + hint + ")" : "[...]";
                    replacement = "<span class=\"cloze\">" + blankText + "</span>";
                } else {
                    // Emphasize on back
                    replacement = "<strong>" + clozeText + "</strong>";
                }
            } else {
                // Show as plain text for other cloze indices
                replacement = clozeText;
            }

            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    private String renderAllClozeAsText(String fieldValue) {
        Matcher matcher = CLOZE_PATTERN.matcher(fieldValue);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String clozeText = matcher.group(2);
            matcher.appendReplacement(result, Matcher.quoteReplacement(clozeText));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    private Set<Integer> getAllClozeIndices(String frontTemplate, String backTemplate, Map<String, String> fieldValues) {
        Set<Integer> indices = new HashSet<>();

        // Get all cloze indices from field values referenced by cloze: templates
        indices.addAll(getClozeIndicesFromTemplate(frontTemplate, fieldValues));
        indices.addAll(getClozeIndicesFromTemplate(backTemplate, fieldValues));

        return indices;
    }

    private Set<Integer> getClozeIndicesFromTemplate(String template, Map<String, String> fieldValues) {
        Set<Integer> indices = new HashSet<>();

        Matcher templateMatcher = FIELD_PATTERN.matcher(template);
        while (templateMatcher.find()) {
            String fieldReference = templateMatcher.group(1);
            if (fieldReference.startsWith("cloze:")) {
                String fieldName = fieldReference.substring(6);
                String fieldValue = fieldValues.getOrDefault(fieldName, "");
                indices.addAll(getClozeIndicesFromFieldValue(fieldValue));
            }
        }

        return indices;
    }

    private Set<Integer> getClozeIndicesFromFieldValue(String fieldValue) {
        Set<Integer> indices = new HashSet<>();
        Matcher matcher = CLOZE_PATTERN.matcher(fieldValue);

        while (matcher.find()) {
            int clozeIndex = Integer.parseInt(matcher.group(1));
            indices.add(clozeIndex);
        }

        return indices;
    }
}