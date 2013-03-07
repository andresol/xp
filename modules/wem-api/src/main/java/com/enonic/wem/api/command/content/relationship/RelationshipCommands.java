package com.enonic.wem.api.command.content.relationship;

public final class RelationshipCommands
{
    public CreateRelationship create()
    {
        return new CreateRelationship();
    }

    public UpdateRelationship update()
    {
        return new UpdateRelationship();
    }

    public DeleteRelationship delete()
    {
        return new DeleteRelationship();
    }

}
