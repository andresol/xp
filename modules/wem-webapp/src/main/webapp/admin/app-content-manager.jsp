<%@ taglib prefix="w" uri="uri:enonic.wem.taglib" %>
<!DOCTYPE html>
<w:helper var="helper"/>
<html>
<head>
  <meta charset="utf-8"/>
  <title>Enonic WEM Admin</title>
  <link rel="stylesheet" type="text/css" href="resources/lib/ext/resources/css/ext-all.css">
  <link rel="stylesheet" type="text/css" href="resources/css/main.css">
  <link rel="stylesheet" type="text/css" href="resources/css/icons.css">
  <link rel="stylesheet" type="text/css" href="resources/css/cms-preview-panel.css">
  <link rel="stylesheet" type="text/css" href="resources/css/admin-tree-panel.css">

  <script type="text/javascript" src="config.js"></script>
  <script type="text/javascript" charset="utf-8">
    window.CONFIG = {
      baseUrl: '<%= helper.getBaseUrl().substring( 0, helper.getBaseUrl().lastIndexOf( "/admin" ) )%>'
    };
  </script>
  <script type="text/javascript" src="resources/lib/ext/ext-all-debug.js"></script>
  <script type="text/javascript" src="resources/app/view/XTemplates.js"></script>
  <script type="text/javascript" src="resources/app/lib/UriHelper.js"></script>
  <script type="text/javascript" src="setup.js"></script>

  <!-- Third party plugins -->
  <script type="text/javascript" src="resources/lib/jit/jit-yc.js"></script>

  <script type="text/javascript">
    Ext.Loader.setConfig({
      enabled: true,
      paths: {
        'Common': 'common/js',
        'Main': '_app/main/js',
        'Admin': 'resources/app'
      },
      disableCaching: false
    });
    Ext.application({
      name: 'App',
      appFolder: '_app/contentManager/js',

      controllers: [
        'Admin.controller.contentManager.GridPanelController',
        'Admin.controller.contentManager.DetailPanelController',
        'Admin.controller.contentManager.FilterPanelController',
        'Admin.controller.contentManager.BrowseToolbarController',
        'Admin.controller.contentManager.ContentWizardController',
        'Admin.controller.contentManager.ContentPreviewController',
        'Admin.controller.contentManager.DialogWindowController'
      ],

      requires: [
        'Admin.view.TabPanel'
      ],

      launch: function () {

        Ext.create('Ext.container.Viewport', {
          layout: 'fit',
          cls: 'admin-viewport',
          padding: 5,

          items: [
            {
              xtype: 'cmsTabPanel',
              items: [
                {
                  id: 'tab-browse',
                  title: 'Browse',
                  closable: false,
                  xtype: 'panel',
                  layout: 'border',
                  items: [
                    {
                      region: 'west',
                      xtype: 'contentFilter',
                      width: 200
                    },
                    {
                      region: 'center',
                      xtype: 'contentShow'
                    }
                  ]
                }
              ]
            }
          ]
        });
      }
    });

  </script>

</head>
<body>
</body>
</html>
