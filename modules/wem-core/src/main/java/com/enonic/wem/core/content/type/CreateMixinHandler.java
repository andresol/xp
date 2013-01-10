package com.enonic.wem.core.content.type;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.type.CreateMixin;
import com.enonic.wem.api.content.type.form.Mixin;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.type.dao.MixinDao;

@Component
public final class CreateMixinHandler
    extends CommandHandler<CreateMixin>
{
    private MixinDao mixinDao;

    public CreateMixinHandler()
    {
        super( CreateMixin.class );
    }

    @Override
    public void handle( final CommandContext context, final CreateMixin command )
        throws Exception
    {
        final Mixin mixin = command.getMixin();
        final Session session = context.getJcrSession();
        mixinDao.createMixin( mixin, session );
        session.save();
        command.setResult( mixin.getQualifiedName() );
    }

    @Autowired
    public void setMixinDao( final MixinDao value )
    {
        this.mixinDao = value;
    }
}
