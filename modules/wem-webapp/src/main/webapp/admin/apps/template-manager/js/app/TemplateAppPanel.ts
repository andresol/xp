module app {

    export class TemplateAppPanel extends api.app.BrowseAndWizardBasedAppPanel<app.browse.TemplateBrowseItem> {

        constructor(appBar: api.app.AppBar) {

            var browsePanel = new app.browse.TemplateBrowsePanel();

            super({
                appBar: appBar,
                browsePanel: browsePanel
            });

            this.handleGlobalEvents();

        }

        private handleGlobalEvents() {

            var templateUploader = new app.imp.TemplateUploader();
            var dialog = new api.ui.dialog.UploadDialog(
                "Template Importer", "Templates will be imported in application", templateUploader
            );
            templateUploader.onFinishUpload((response: api.content.site.template.InstallSiteTemplateResponse) => {
                var templates = response.getSiteTemplates();
                if (templates.length > 0) {
                    api.notify.showFeedback('Template \'' + templates.map((template: api.content.site.template.SiteTemplateSummary) => {
                        console.log(template);
                        return template.getDisplayName()
                    }).join(', ') + '\' was installed');
                }
                new api.content.site.template.SiteTemplateImportedEvent().fire();
                dialog.close();
            });
            templateUploader.onError((resp: api.rest.RequestError) => {
                api.notify.showError("Invalid Template file");
                dialog.close();
            });

            app.browse.event.ImportTemplateEvent.on(() => {
                dialog.open();
            });

            app.browse.event.ExportTemplateEvent.on((event: app.browse.event.ExportTemplateEvent) => {
                var siteTemplate: api.content.site.template.SiteTemplateSummary = event.getSiteTemplate();

                var exportTemplate = new api.content.site.template.ExportSiteTemplateRequest(siteTemplate.getKey());
                var templateExportUrl = exportTemplate.getRequestPath().toString() + '?siteTemplateKey=' + siteTemplate.getKey().toString();
                console.log('Download Site Template file from: ' + templateExportUrl);

                window.location.href = templateExportUrl;
            });

            app.browse.event.NewTemplateEvent.on((event: app.browse.event.NewTemplateEvent) => {
                var tabId = api.app.AppBarTabId.forNew('new-site-template-wizard');
                var tabMenuItem = new api.app.AppBarTabMenuItem("New Site Template ", tabId);
                var wizard = new app.wizard.SiteTemplateWizardPanel(tabId);
                this.addWizardPanel(tabMenuItem, wizard);
            });

            app.browse.event.EditTemplateEvent.on((event: app.browse.event.EditTemplateEvent) => {
                event.getTemplates().forEach((template: api.content.site.template.SiteTemplateSummary) => {

                    new api.content.site.template.GetSiteTemplateRequest(template.getKey()).sendAndParse().
                        done((siteTemplate: api.content.site.template.SiteTemplate)=> {

                            var tabId = api.app.AppBarTabId.forEdit(template.getId());
                            var tabMenuItem = new api.app.AppBarTabMenuItem("Edit Site Template", tabId);
                            var wizard = new app.wizard.SiteTemplateWizardPanel(tabId, siteTemplate);
                            this.addWizardPanel(tabMenuItem, wizard);
                        });

                });
            });
        }
    }
}
