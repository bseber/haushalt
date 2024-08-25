package org.bseber.haushalt.tags;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

class TagBucketDto {

    @Valid
    @NotEmpty
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
