package com.enonic.wem.api.item;

public interface ItemEditor
{
    /**
     * @param toBeEdited to be edited
     * @return updated item, null if it has no change was necessary.
     */
    public Item edit( Item toBeEdited );
}
