package parsing;

import java.util.ArrayList;
import java.util.List;

public class Tree {
    String label;
    List<Tree> children;

    public Tree(String label) {
        children = new ArrayList<>();
        this.label = label;
    }

    public Tree(String label, List<Tree> children) {
        this.children = children;
        this.label = label;
    }

    @Override
    public String toString() {
        return "T{" +
                 label + "-" +
                 children +
                '}';
    }
}
