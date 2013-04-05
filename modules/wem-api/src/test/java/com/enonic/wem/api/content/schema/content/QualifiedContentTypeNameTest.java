package com.enonic.wem.api.content.schema.content;


import org.junit.Test;

import com.enonic.wem.api.content.AbstractEqualsTest;

import static org.junit.Assert.*;

public class QualifiedContentTypeNameTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return new QualifiedContentTypeName( "myModule:myContentType" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{new QualifiedContentTypeName( "myModule:myOtherContentType" ),
                    new QualifiedContentTypeName( "myOtherModule:myContentType" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return new QualifiedContentTypeName( "myModule:myContentType" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return new QualifiedContentTypeName( "myModule:myContentType" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void testPredefinedTypes()
    {
        assertTrue( QualifiedContentTypeName.media().isMedia() );
        assertTrue( QualifiedContentTypeName.folder().isFolder() );
        assertTrue( QualifiedContentTypeName.page().isPage() );
        assertTrue( QualifiedContentTypeName.shortcut().isShortcut() );
        assertTrue( QualifiedContentTypeName.space().isSpace() );
        assertTrue( QualifiedContentTypeName.structured().isStructured() );
        assertTrue( QualifiedContentTypeName.unstructured().isUnstructured() );

        assertTrue( QualifiedContentTypeName.textMedia().isTextMedia() );
        assertTrue( QualifiedContentTypeName.dataMedia().isDataMedia() );
        assertTrue( QualifiedContentTypeName.audioMedia().isAudioMedia() );
        assertTrue( QualifiedContentTypeName.videoMedia().isVideoMedia() );
        assertTrue( QualifiedContentTypeName.imageMedia().isImageMedia() );
        assertTrue( QualifiedContentTypeName.vectorMedia().isVectorMedia() );
        assertTrue( QualifiedContentTypeName.archiveMedia().isArchiveMedia() );
        assertTrue( QualifiedContentTypeName.documentMedia().isDocumentMedia() );
        assertTrue( QualifiedContentTypeName.spreadsheetMedia().isSpreadsheetMedia() );
        assertTrue( QualifiedContentTypeName.presentationMedia().isPresentationMedia() );
        assertTrue( QualifiedContentTypeName.codeMedia().isCodeMedia() );
        assertTrue( QualifiedContentTypeName.executableMedia().isExecutableMedia() );

        assertEquals( "media", QualifiedContentTypeName.media().getContentTypeName() );
        assertEquals( "folder", QualifiedContentTypeName.folder().getContentTypeName() );
        assertEquals( "page", QualifiedContentTypeName.page().getContentTypeName() );
        assertEquals( "shortcut", QualifiedContentTypeName.shortcut().getContentTypeName() );
        assertEquals( "space", QualifiedContentTypeName.space().getContentTypeName() );
        assertEquals( "structured", QualifiedContentTypeName.structured().getContentTypeName() );
        assertEquals( "unstructured", QualifiedContentTypeName.unstructured().getContentTypeName() );

        assertEquals( "text", QualifiedContentTypeName.textMedia().getContentTypeName() );
        assertEquals( "data", QualifiedContentTypeName.dataMedia().getContentTypeName() );
        assertEquals( "audio", QualifiedContentTypeName.audioMedia().getContentTypeName() );
        assertEquals( "video", QualifiedContentTypeName.videoMedia().getContentTypeName() );
        assertEquals( "image", QualifiedContentTypeName.imageMedia().getContentTypeName() );
        assertEquals( "vector", QualifiedContentTypeName.vectorMedia().getContentTypeName() );
        assertEquals( "archive", QualifiedContentTypeName.archiveMedia().getContentTypeName() );
        assertEquals( "document", QualifiedContentTypeName.documentMedia().getContentTypeName() );
        assertEquals( "spreadsheet", QualifiedContentTypeName.spreadsheetMedia().getContentTypeName() );
        assertEquals( "presentation", QualifiedContentTypeName.presentationMedia().getContentTypeName() );
        assertEquals( "code", QualifiedContentTypeName.codeMedia().getContentTypeName() );
        assertEquals( "executable", QualifiedContentTypeName.executableMedia().getContentTypeName() );
    }
}
