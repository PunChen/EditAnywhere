package com.example.editanywhere.entity.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Content {
    private String content;
    private Boolean checked;

    public Content(String content, Boolean checked) {
        this.content = content;
        this.checked = checked;
    }

    public Content(String content) {
        this.checked = false;
        this.content = content;
    }

    public Content() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }
}
