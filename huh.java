import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.collections4.Equator

import static com.fasterxml.jackson.databind.node.JsonNodeType.*
import static org.apache.commons.collections4.CollectionUtils.isEqualCollection

class Compare implements Comparator<String> {
    private static ObjectMapper om = new ObjectMapper()

    private boolean ignoreElementOrderInArrays = true

    @Override
    int compare(String o1, String o2) {
        return compare(om.readTree(o1), om.readTree(o2)) ? 0 : -1
    }

    boolean compare(Iterable<? extends JsonNode> o1, Iterable<? extends JsonNode> o2) {
        if (o1 == null || o2 == null) {
            return false
        }
        if (o1 == o2) {
            return true
        }
        if (o1 instanceof JsonNode && o2 instanceof JsonNode) {
            return compareJsonNodes((JsonNode) o1, (JsonNode) o2)
        }
        return false
    }

    private static boolean compareJsonNodes(JsonNode o1, JsonNode o2) {
        if (o1 == null || o2 == null) {
            return false
        }
        if (o1 == o2) {
            return true
        }
        if (!o1.getNodeType().equals(o2.getNodeType())) {
            return false
        }
        switch (o1.getNodeType()) {
            case NULL:
            case BOOLEAN:
            case STRING:
            case NUMBER:
                return o1.asText() == o2.asText()
            case OBJECT:
                // ignores fields with null value that are missing at other JSON
                def l = o1.fieldNames().toSet()
                def r = o2.fieldNames().toSet()
                def missing = (l + r).findAll { !compareJsonNodes(o1.get(it), o2.get(it)) }
                return !missing
            case ARRAY:
                if (o1.size() != o2.size()) {
                    return false
                }
                if (o1.isEmpty()) {
                    return true
                }
                def o1Elements = o1.elements().toList()
                def o2Elements = o2.elements().toList()

                def equal = isEqualCollection(o1Elements, o2Elements, new CachingEquator());
                return equal
            default:
                return false
        }
    }

    static class CachingEquator implements Equator<JsonNode> {

        Map<JsonNode, Map<JsonNode, Boolean>> cache = new HashMap<>()

        @Override
        boolean equate(JsonNode o1, JsonNode o2) {

            if (!cache.containsKey(o1)) {
                cache.put(o1, new HashMap<JsonNode, Boolean>());
            }

            if (!cache.get(o1).containsKey(o2)) {
                boolean equates = compareJsonNodes(o1, o2)
                cache.get(o1).put(o2, equates);
                return equates
            }

            return (cache.get(o1).get(o2));
        }

        @Override
        int hash(JsonNode o) {
            return o.hashCode()
        }
    }

    private boolean lookForMissingElement(JsonNode elementToLookFor, Collection<JsonNode> elements) {
        return elements.find { compareJsonNodes(elementToLookFor, it) } == null
    }


    public static <T extends Comparable<T>> void printAllOrdered(
            T[] elements) {

        Arrays.sort(elements);
        boolean hasNext = true;

        while (hasNext) {
            println(new ArrayList(elements)).join(', ')
            int k = 0, l = 0;
            hasNext = false;
            for (int i = elements.length - 1; i > 0; i--) {
                if (elements[i].compareTo(elements[i - 1]) > 0) {
                    k = i - 1;
                    hasNext = true;
                    break;
                }
            }

            for (int i = elements.length - 1; i > k; i--) {
                if (elements[i].compareTo(elements[k]) > 0) {
                    l = i;
                    break;
                }
            }

            swap(elements, k, l);
            Collections.reverse(Arrays.asList(elements).subList(k + 1, elements.length));
        }
    }

}
