package net.minecraft.util;

import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSectionSerializer;

public class MetadataSectionEntry<T extends IMetadataSection> {
    private final IMetadataSectionSerializer<T> serializer;
    private final Class<T> sectionType;

    public MetadataSectionEntry(IMetadataSectionSerializer<T> serializer, Class<T> sectionType) {
        this.serializer = serializer;
        this.sectionType = sectionType;
    }

    public IMetadataSectionSerializer<T> getSerializer() {
        return serializer;
    }

    public Class<T> getSectionType() {
        return sectionType;
    }
}