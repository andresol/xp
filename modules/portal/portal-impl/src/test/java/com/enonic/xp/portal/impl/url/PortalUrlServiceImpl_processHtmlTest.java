package com.enonic.xp.portal.impl.url;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.Media;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.portal.url.ProcessHtmlParams;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.web.servlet.ServletRequestHolder;

import static org.junit.Assert.*;

public class PortalUrlServiceImpl_processHtmlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Before
    public void before()
    {

    }

    @Test
    public void process_empty_value()
    {
        //Checks the process for a null value
        final ProcessHtmlParams params = new ProcessHtmlParams().
            portalRequest( this.portalRequest );
        String processedHtml = this.service.processHtml( params );
        assertEquals( "", processedHtml );

        //Checks the process for an empty string value
        params.value( "" );
        processedHtml = this.service.processHtml( params );
        assertEquals( "", processedHtml );
    }

    @Test
    public void process_single_content()
    {
        //Creates a content
        final Content content = ContentFixtures.newContent();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        //Process an html text containing a link to this content
        final ProcessHtmlParams params = new ProcessHtmlParams().
            portalRequest( this.portalRequest ).
            value( "<a href=\"content://" + content.getId() + "\">Content</a>" );

        //Checks that the page URL of the content is returned
        final String processedHtml = this.service.processHtml( params );
        assertEquals( "<a href=\"/portal/draft" + content.getPath() + "\">Content</a>", processedHtml );
    }

    @Test
    public void process_single_image()
    {
        //Creates a content
        final Media media = ContentFixtures.newMedia();
        Mockito.when( this.contentService.getById( media.getId() ) ).thenReturn( media );
        Mockito.when( this.contentService.getBinaryKey( media.getId(), media.getMediaAttachment().getBinaryReference() ) ).thenReturn(
            "binaryHash" );

        //Process an html text containing a link to this content
        final ProcessHtmlParams params = new ProcessHtmlParams().
            portalRequest( this.portalRequest ).
            value( "<a href=\"image://" + media.getId() + "\">Image</a>" );

        //Checks that the page URL of the content is returned
        final String processedHtml = this.service.processHtml( params );
        assertEquals(
            "<a href=\"/portal/draft/context/path/_/image/" + media.getId() + ":992a0004e50e58383fb909fea2b588dc714a7115/" + "full" +
                "/" + media.getName() +
                "\">Image</a>", processedHtml );
    }

    @Test
    public void process_image_with_keepsize()
    {
        //Creates a content
        final Media media = ContentFixtures.newMedia();
        Mockito.when( this.contentService.getById( media.getId() ) ).thenReturn( media );
        Mockito.when( this.contentService.getBinaryKey( media.getId(), media.getMediaAttachment().getBinaryReference() ) ).thenReturn(
            "binaryHash" );

        //Process an html text containing a link to this content
        final ProcessHtmlParams params = new ProcessHtmlParams().
            portalRequest( this.portalRequest ).
            value( "<a href=\"image://" + media.getId() + "?keepsize=true\">Image</a>" );

        //Checks that the page URL of the content is returned
        final String processedHtml = this.service.processHtml( params );
        assertEquals(
            "<a href=\"/portal/draft/context/path/_/image/" + media.getId() + ":992a0004e50e58383fb909fea2b588dc714a7115/" + "width-768" +
                "/" + media.getName() + "\">Image</a>", processedHtml );
    }

    @Test
    public void process_single_media()
    {
        //Creates a content with attachments
        final Attachment thumb = Attachment.
            create().
            label( "thumb" ).
            name( "a1.jpg" ).
            mimeType( "image/jpg" ).
            build();
        final Attachment source = Attachment.
            create().
            label( "source" ).
            name( "a2.jpg" ).
            mimeType( "image/jpg" ).
            build();
        final Attachments attachments = Attachments.from( thumb, source );
        final Content content = Content.
            create( ContentFixtures.newContent() ).
            attachments( attachments ).
            build();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );
        Mockito.when( this.contentService.getBinaryKey( content.getId(), source.getBinaryReference() ) ).thenReturn( "binaryHash2" );

        //Process an html text containing an inline link to this content
        ProcessHtmlParams params = new ProcessHtmlParams().
            portalRequest( this.portalRequest ).
            value( "<a href=\"media://inline/" + content.getId() + "\">Media</a>" );

        //Checks that the URL of the source attachment of the content is returned
        String processedHtml = this.service.processHtml( params );
        assertEquals( "<a href=\"/portal/draft/context/path/_/attachment/inline/" + content.getId() + ":binaryHash2/" + source.getName() +
                          "\">Media</a>", processedHtml );

        //Process an html text containing a download link to this content
        params = new ProcessHtmlParams().
            portalRequest( this.portalRequest ).
            value( "<a href=\"media://download/" + content.getId() + "\">Media</a>" );

        //Checks that the URL of the source attachment of the content is returned
        processedHtml = this.service.processHtml( params );
        assertEquals( "<a href=\"/portal/draft/context/path/_/attachment/download/" + content.getId() + ":binaryHash2/" + source.getName() +
                          "\">Media</a>", processedHtml );

        //Process an html text containing an inline link to this content in a img tag
        params = new ProcessHtmlParams().
            portalRequest( this.portalRequest ).
            value( "<a href=\"/some/page\"><img src=\"media://inline/" + content.getId() + "\">Media</a>" );

        //Checks that the URL of the source attachment of the content is returned
        processedHtml = this.service.processHtml( params );
        assertEquals(
            "<a href=\"/some/page\"><img src=\"/portal/draft/context/path/_/attachment/inline/" + content.getId() + ":binaryHash2/" +
                source.getName() +
                "\">Media</a>", processedHtml );

    }

    @Test
    public void process_multiple_links()
    {
        //Creates a content with attachments
        final Attachment thumb = Attachment.
            create().
            label( "thumb" ).
            name( "a1.jpg" ).
            mimeType( "image/jpg" ).
            build();
        final Attachment source = Attachment.
            create().
            label( "source" ).
            name( "a2.jpg" ).
            mimeType( "image/jpg" ).
            build();
        final Attachments attachments = Attachments.from( thumb, source );
        final Content content = Content.
            create( ContentFixtures.newContent() ).
            attachments( attachments ).
            build();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );
        Mockito.when( this.contentService.getBinaryKey( content.getId(), source.getBinaryReference() ) ).thenReturn( "binaryHash2" );

        //Process an html text containing multiple links, on multiple lines, to this content as a media and as a content
        final ProcessHtmlParams params = new ProcessHtmlParams().
            portalRequest( this.portalRequest ).
            value( "<p>A content link:&nbsp;<a href=\"content://" + content.getId() + "\">FirstLink</a></p>\n" +
                       "<p>A second content link:&nbsp;<a href=\"content://" + content.getId() + "\">SecondLink</a>" +
                       "&nbsp;and a download link:&nbsp;<a href=\"media://download/" + content.getId() + "\">Download</a></p>\n" +
                       "<p>An external link:&nbsp;<a href=\"http://www.enonic.com\">An external  link</a></p>\n" +
                       "<p>&nbsp;</p>\n" +
                       "<a href=\"media://inline/" + content.getId() + "\">Inline</a>" );

        //Checks the returned value
        final String processedHtml = this.service.processHtml( params );
        assertEquals( "<p>A content link:&nbsp;<a href=\"/portal/draft" + content.getPath() + "\">FirstLink</a></p>\n" +
                          "<p>A second content link:&nbsp;<a href=\"/portal/draft" + content.getPath() + "\">SecondLink</a>" +
                          "&nbsp;and a download link:&nbsp;<a href=\"/portal/draft/context/path/_/attachment/download/" +
                          content.getId() + ":binaryHash2/" + source.getName() + "\">Download</a></p>\n" +
                          "<p>An external link:&nbsp;<a href=\"http://www.enonic.com\">An external  link</a></p>\n" +
                          "<p>&nbsp;</p>\n" +
                          "<a href=\"/portal/draft/context/path/_/attachment/inline/" +
                          content.getId() + ":binaryHash2/" + source.getName() + "\">Inline</a>", processedHtml );
    }

    @Test
    public void process_unknown_content()
    {

        //Process an html text containing a link to an unknown content
        final ProcessHtmlParams params = new ProcessHtmlParams().
            portalRequest( this.portalRequest ).
            value( "<a href=\"content://123\">Content</a>" );

        //Checks that the error 500 page is returned
        final String processedHtml = this.service.processHtml( params );
        assertEquals( "<a href=\"/portal/draft/context/path/_/error/500\">Content</a>", processedHtml );
    }

    @Test
    public void process_unknown_media()
    {

        //Process an html text containing a link to an unknown media
        final ProcessHtmlParams params = new ProcessHtmlParams().
            portalRequest( this.portalRequest ).
            value( "<a href=\"media://inline/123\">Media</a>" );

        //Checks that the error 500 page is returned
        final String processedHtml = this.service.processHtml( params );
        assertEquals( "<a href=\"/portal/draft/context/path/_/error/500\">Media</a>", processedHtml );
    }

    @Test
    public void process_unknown_image()
    {

        //Process an html text containing a link to an unknown media
        final ProcessHtmlParams params = new ProcessHtmlParams().
            portalRequest( this.portalRequest ).
            value( "<a href=\"image://123\">Image</a>" );

        //Checks that the error 500 page is returned
        final String processedHtml = this.service.processHtml( params );
        assertEquals( "<a href=\"/portal/draft/context/path/_/error/500\">Image</a>", processedHtml );
    }

    @Test
    public void process_absolute()
    {
        //Creates a content
        final Content content = ContentFixtures.newContent();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        //Process an html text containing a link to this content
        final ProcessHtmlParams params = new ProcessHtmlParams().
            type( UrlTypeConstants.ABSOLUTE ).
            portalRequest( this.portalRequest ).
            value( "<a href=\"content://" + content.getId() + "\">Content</a>" );

        MockHttpServletRequest req = new MockHttpServletRequest();
        ServletRequestHolder.setRequest( req );

        //Checks that the page URL of the content is returned
        final String processedHtml = this.service.processHtml( params );
        assertEquals( "<a href=\"http://localhost/portal/draft" + content.getPath() + "\">Content</a>", processedHtml );
    }

    @Test
    @Ignore
    public void process_html_with_script()
    {
        //Process an html text containing a link to this content
        final ProcessHtmlParams params = new ProcessHtmlParams().
            type( UrlTypeConstants.ABSOLUTE ).
            portalRequest( this.portalRequest ).
            value( "<a href=\"/some/path\"><script>alert('test')</script>Content</a><script>alert('test')</script>" );

        MockHttpServletRequest req = new MockHttpServletRequest();
        ServletRequestHolder.setRequest( req );

        //Checks that the page URL of the content is returned
        final String processedHtml = this.service.processHtml( params );
        assertEquals( "<a href=\"/some/path\">Content</a>", processedHtml );
    }

    @Test
    public void process_image_with_scale()
    {
        //Creates a content
        final Media media = ContentFixtures.newMedia();
        Mockito.when( this.contentService.getById( media.getId() ) ).thenReturn( media );
        Mockito.when( this.contentService.getBinaryKey( media.getId(), media.getMediaAttachment().getBinaryReference() ) ).thenReturn(
            "binaryHash" );

        //Process an html text containing a link to this content
        final ProcessHtmlParams params = new ProcessHtmlParams().
            portalRequest( this.portalRequest ).
            value( "<a href=\"image://" + media.getId() + "?scale=21:9&amp;keepSize=true\">Image</a>" );

        //Checks that the page URL of the content is returned
        final String processedHtml = this.service.processHtml( params );
        assertEquals( "<a href=\"/portal/draft/context/path/_/image/" + media.getId() + ":992a0004e50e58383fb909fea2b588dc714a7115/" +
                          "block-300-126" +
                          "/" + media.getName() +
                          "\">Image</a>", processedHtml );
    }

    @Test
    public void process_html_with_macros()
    {
        final ProcessHtmlParams params1 = new ProcessHtmlParams().
            value( "<a href=\"/some/path\">[macroName par1=\"val1\" par2=\"val2\"]body body[/macroName]</a>" );

        final ProcessHtmlParams params2 = new ProcessHtmlParams().
            value( "<a href=\"/some/path\">[macroName par1=\"val1\" par2=\"val2\"/]</a>" );

        final ProcessHtmlParams params3 = new ProcessHtmlParams().
            value( "<a href=\"[macroNoBody /]\">[macro par1=\"val1\" par2=\"val2\"/]</a> \\[macroName]skip me[/macroName]" );

        final ProcessHtmlParams params4 = new ProcessHtmlParams().
            value( "<p>[macroName par1=\"val1\"]body [macroInBody]macroInBody[/macroInBody] body[/macroName]</p>" );

        final ProcessHtmlParams params5 = new ProcessHtmlParams().
            value( "<a href=\"[macro /]\">body</a>" );

        final ProcessHtmlParams params6 = new ProcessHtmlParams().
            value( "\\[macro /] \\[macro][/macro] \\ [macro /]" );

        final ProcessHtmlParams params7 = new ProcessHtmlParams().
            value( "[macro_name][macro_in_body/][/macro_name]" );

        final String processedHtml1 = this.service.processHtml( params1 );
        final String processedHtml2 = this.service.processHtml( params2 );
        final String processedHtml3 = this.service.processHtml( params3 );
        final String processedHtml4 = this.service.processHtml( params4 );
        final String processedHtml5 = this.service.processHtml( params5 );
        final String processedHtml6 = this.service.processHtml( params6 );
        final String processedHtml7 = this.service.processHtml( params7 );

        assertEquals( "<a href=\"/some/path\"><!--#MACRO _name=\"macroName\" par1=\"val1\" par2=\"val2\" _body=\"body body\"--></a>",
                      processedHtml1 );
        assertEquals( "<a href=\"/some/path\"><!--#MACRO _name=\"macroName\" par1=\"val1\" par2=\"val2\" _body=\"\"--></a>",
                      processedHtml2 );
        assertEquals(
            "<a href=\"<!--#MACRO _name=\"macroNoBody\" _body=\"\"-->\"><!--#MACRO _name=\"macro\" par1=\"val1\" par2=\"val2\" _body=\"\"--></a> \\[macroName]skip me[/macroName]",
            processedHtml3 );
        assertEquals( "<p><!--#MACRO _name=\"macroName\" par1=\"val1\" _body=\"body [macroInBody]macroInBody[/macroInBody] body\"--></p>",
                      processedHtml4 );
        assertEquals( "<a href=\"<!--#MACRO _name=\"macro\" _body=\"\"-->\">body</a>", processedHtml5 );

        assertEquals( "\\[macro /] \\[macro][/macro] \\ [macro /]", processedHtml6 );

        assertEquals( "<!--#MACRO _name=\"macroName\" par1=\"val1\" par2=\"val2\" _body=\"[macro_in_body/]\"-->", processedHtml7 );
    }
}
