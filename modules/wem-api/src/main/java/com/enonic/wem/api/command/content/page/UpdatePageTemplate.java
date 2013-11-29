package com.enonic.wem.api.command.content.page;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;

public final class UpdatePageTemplate
    extends Command<Boolean>
{
    private PageTemplateKey key;

    private TemplateEditor<PageTemplate> editor;

    public UpdatePageTemplate key( final PageTemplateKey key )
    {
        this.key = key;
        return this;
    }

    public UpdatePageTemplate editor( final TemplateEditor<PageTemplate> editor )
    {
        this.editor = editor;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( key, "key is required" );
    }

    public PageTemplateKey getKey()
    {
        return key;
    }

    public TemplateEditor<PageTemplate> getEditor()
    {
        return editor;
    }
}
