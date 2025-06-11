package com.iyuba.talkshow.data.model;

/**
 * CategoryFooter
 *
 * @author wayne
 * @date 2018/2/2
 */
public class CategoryFooter implements RecyclerItem {
    private int pos;
    private final int type;
    private final String category;

    public CategoryFooter(int pos, int type, String category) {
        this.pos = pos;
        this.type = type;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public int getPos() {
        return pos;
    }

    public int getType() {
        return type;
    }

    public void setPos(int adapterPosition) {
        this.pos = adapterPosition;
    }
}
