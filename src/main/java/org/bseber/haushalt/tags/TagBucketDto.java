package org.bseber.haushalt.tags;

import java.util.List;

class TagBucketDto {

    private List<TagDto> tags;

    public TagBucketDto(List<TagDto> tags) {
        this.tags = tags;
    }

    public List<TagDto> getTags() {
        return tags;
    }

    public void setTags(List<TagDto> tags) {
        this.tags = tags;
    }
}
