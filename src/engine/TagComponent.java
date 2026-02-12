package engine;

import engine.collision.Vector2D;
import java.util.ArrayList;
import java.util.List;

public class TagComponent extends Component {
    private List<String> tags;
    
    public TagComponent() {
        this.tags = new ArrayList<>();
    }
    
    public TagComponent(String... tags) {
        this.tags = new ArrayList<>();
        for (String tag : tags) {
            this.tags.add(tag);
        }
    }
    
    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    public void removeTag(String tag) {
        tags.remove(tag);
    }
    
    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }
    
    public List<String> getTags() {
        return new ArrayList<>(tags);
    }
    
    public void clearTags() {
        tags.clear();
    }
    
    @Override
    public void update(double deltaTime) {
        // Tags don't need updates
    }
    
    @Override
    public void start() {
        // Tags don't need initialization
    }
    
    @Override
    public void destroy() {
        tags.clear();
    }
}