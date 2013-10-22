module app_wizard_form {

    export class FormView extends api_ui.Panel {

        private form:api_form.Form;

        private contentData:api_content.ContentData;

        private formItemViews:FormItemView[] = [];

        constructor(form:api_form.Form, contentData?:api_content.ContentData) {
            super("FormView");
            this.setClass("form-view");
            this.form = form;
            this.contentData = contentData;
            this.doLayout();
        }

        private doLayout() {

            if (this.contentData == null) {
                this.contentData = new api_content.ContentData();

                this.form.getFormItems().forEach((formItem:api_form.FormItem) => {
                    if (formItem instanceof api_form.FieldSet) {
                        var fieldSet:api_form.FieldSet = <api_form.FieldSet>formItem;
                        console.log("FormView.doLayout() laying out FieldSet: ", fieldSet);
                        var fieldSetView = new app_wizard_form_layout.FieldSetView(fieldSet);
                        this.appendChild(fieldSetView);
                        this.formItemViews.push(fieldSetView);
                    }
                    else if (formItem instanceof api_form.FormItemSet) {
                        var formItemSet:api_form.FormItemSet = <api_form.FormItemSet>formItem;
                        console.log("FormView.doLayout() laying out FormItemSet: ", formItemSet);
                        var formItemSetView = new app_wizard_form_formitemset.FormItemSetView(formItemSet);
                        this.appendChild(formItemSetView);
                        this.formItemViews.push(formItemSetView);
                    }
                    else if (formItem instanceof api_form.Input) {
                        var input:api_form.Input = <api_form.Input>formItem;
                        console.log("FormView.doLayout() laying out Input: ", input);
                        var inputContainerView = new app_wizard_form_input.InputView(input);
                        this.appendChild(inputContainerView);
                        this.formItemViews.push(inputContainerView);
                    }
                });
            }
            else {
                this.form.getFormItems().forEach((formItem:api_form.FormItem) => {
                    if (formItem instanceof api_form.FormItemSet) {
                        var formItemSet:api_form.FormItemSet = <api_form.FormItemSet>formItem;
                        console.log("FormView.doLayout() laying out FormItemSet: ", formItemSet);
                        var dataSets:api_data.DataSet[] = this.contentData.getDataSetsByName(formItemSet.getName());
                        var formItemSetView = new app_wizard_form_formitemset.FormItemSetView(formItemSet, dataSets);
                        this.appendChild(formItemSetView);
                        this.formItemViews.push(formItemSetView);
                    }
                    else if (formItem instanceof api_form.Input) {
                        var input:api_form.Input = <api_form.Input>formItem;
                        console.log("FormView.doLayout() laying out Input: ", input);
                        var properties:api_data.Property[] = this.contentData.getPropertiesByName(input.getName());
                        var inputContainerView = new app_wizard_form_input.InputView(input, properties);
                        this.appendChild(inputContainerView);
                        this.formItemViews.push(inputContainerView);
                    }
                });
            }

        }

        getContentData():api_content.ContentData {
            return this.contentData;
        }

        rebuildContentData():api_content.ContentData {
            var contentData:api_content.ContentData = new api_content.ContentData();
            this.formItemViews.forEach((formItemContainer:FormItemView) => {

                formItemContainer.getData().forEach((data:api_data.Data) => {
                    contentData.addData(data)
                });

            });
            return contentData;
        }
    }
}