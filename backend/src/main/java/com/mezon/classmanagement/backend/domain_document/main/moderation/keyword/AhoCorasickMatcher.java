package com.mezon.classmanagement.backend.domain_document.main.moderation.keyword;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class AhoCorasickMatcher {

    private static class Node {

        Map<Character, Node> children = new HashMap<>();

        Node failure;

        boolean isEnd;
    }

    private final Node root = new Node();

    public AhoCorasickMatcher(List<String> keywords) {
        buildTrie(keywords);
        buildFailureLinks();
    }

    /**
     * Bước 1:
     * Xây dựng Trie từ danh sách keyword.
     */
    private void buildTrie(List<String> keywords) {

        for (String keyword : keywords) {

            if (keyword == null || keyword.isBlank()) {
                continue;
            }

            Node current = root;

            for (char character : keyword.toCharArray()) {

                current = current.children.computeIfAbsent(
                        character,
                        key -> new Node()
                );
            }

            current.isEnd = true;
        }
    }

    /**
     * Bước 2:
     * Xây dựng failure link cho từng node.
     */
    private void buildFailureLinks() {

        Queue<Node> queue = new LinkedList<>();

        root.failure = root;

        // Các node con trực tiếp của root
        // có failure link trỏ về root.
        for (Node child : root.children.values()) {

            child.failure = root;
            queue.add(child);
        }

        while (!queue.isEmpty()) {

            Node current = queue.poll();

            for (Map.Entry<Character, Node> entry
                    : current.children.entrySet()) {

                char character = entry.getKey();
                Node child = entry.getValue();

                Node failure = current.failure;

                /*
                 * Nếu không tìm thấy ký tự tiếp theo,
                 * đi theo failure link.
                 */
                while (
                        failure != root
                                && !failure.children.containsKey(character)
                ) {
                    failure = failure.failure;
                }

                /*
                 * Nếu root có đường đi bằng character
                 * thì failure của child trỏ tới node đó.
                 */
                if (failure.children.containsKey(character)
                        && failure.children.get(character) != child) {

                    child.failure = failure.children.get(character);

                } else {

                    child.failure = root;
                }

                /*
                 * Nếu failure node là kết thúc của keyword
                 * thì node hiện tại cũng được xem là match.
                 */
                if (child.failure.isEnd) {
                    child.isEnd = true;
                }

                queue.add(child);
            }
        }
    }

    /**
     * Kiểm tra text có chứa ít nhất một keyword bị cấm hay không.
     *
     * @return true  -> có keyword cấm
     * @return false -> không có keyword cấm
     */
    public boolean containsAny(String text) {

        if (text == null || text.isEmpty()) {
            return false;
        }

        Node current = root;

        for (char character : text.toCharArray()) {

            /*
             * Không có đường đi thì đi theo failure link.
             */
            while (
                    current != root
                            && !current.children.containsKey(character)
            ) {
                current = current.failure;
            }

            /*
             * Nếu có đường đi thì đi sang node tiếp theo.
             */
            if (current.children.containsKey(character)) {
                current = current.children.get(character);
            }

            /*
             * Đã gặp keyword bị cấm.
             */
            if (current.isEnd) {
                return true;
            }
        }

        return false;
    }
}