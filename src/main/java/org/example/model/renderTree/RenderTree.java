package org.example.model.renderTree;

public class RenderTree {
    private RenderNode root;

    public RenderTree(RenderNode root) {
        this.root = root;
    }

    public RenderNode getRoot() {
        return root;
    }

    public void renderTree(RenderNode node) {
        if (node == null) {
            return;
        }
        // Вывести текущий узел.
        System.out.println(node);

        // Рекурсивно обработать дочерние узлы.
        for (RenderNode child : node.getChildren()) {
            renderTree(child);
        }
    }

    public void render() {
        System.out.println("Rendering the tree:");
        renderTree(root);
    }
}