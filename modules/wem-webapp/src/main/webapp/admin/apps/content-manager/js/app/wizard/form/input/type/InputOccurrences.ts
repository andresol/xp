module app_wizard_form_input_type {

    /*
     * A kind of a controller, which add/remove InputOccurrenceView-s to the BaseInputTypeView
     */
    export class InputOccurrences extends app_wizard_form.FormItemOccurrences {

        private baseInputTypeView:BaseInputTypeView;

        private input:api_form.Input;

        private properties:api_data.Property[];

        constructor(baseInputTypeView:BaseInputTypeView, input:api_form.Input, properties:api_data.Property[]) {
            super(input, baseInputTypeView, input.getOccurrences());

            this.baseInputTypeView = baseInputTypeView;
            this.input = input;
            this.properties = properties;

            if (properties != null) {
                this.constructOccurrencesForData();
            }
            else {
                this.constructOccurrencesForNoData();
            }
        }

        getInput():api_form.Input {
            return this.input;
        }

        getAllowedOccurrences():api_form.Occurrences {
            return this.input.getOccurrences();
        }

        private constructOccurrencesForData() {
            this.properties.forEach((property:api_data.Property, index:number) => {
                this.addOccurrence(new InputOccurrence(this, index));
            });

            if (this.countOccurrences() < this.input.getOccurrences().getMinimum()) {
                for (var index:number = this.countOccurrences();
                     index < this.input.getOccurrences().getMinimum(); index++) {
                    this.addOccurrence(this.createNewOccurrence(this, index));
                }
            }
        }

        createNewOccurrence(formItemOccurrences:app_wizard_form.FormItemOccurrences,
                            insertAtIndex:number):app_wizard_form.FormItemOccurrence {
            return new InputOccurrence(<InputOccurrences>formItemOccurrences, insertAtIndex)
        }

        createNewOccurrenceView(occurrence:InputOccurrence):InputOccurrenceView {

            var property:api_data.Property = this.properties != null ? this.properties[occurrence.getIndex()] : null;
            var inputElement = this.baseInputTypeView.createInputOccurrenceElement(occurrence.getIndex(), property);

            var inputOccurrenceView:InputOccurrenceView = new InputOccurrenceView(occurrence, inputElement);

            var inputOccurrences:InputOccurrences = this;
            inputOccurrenceView.addListener(<app_wizard_form.FormItemOccurrenceViewListener>{
                onRemoveButtonClicked: (toBeRemoved:InputOccurrenceView, index:number) => {
                    inputOccurrences.doRemoveOccurrence(toBeRemoved, index);
                }
            });
            return inputOccurrenceView;
        }

        getValues():api_data.Value[] {

            var values:api_data.Value[] = [];
            this.getOccurrenceViews().forEach((occurrenceView:InputOccurrenceView) => {
                values.push(this.baseInputTypeView.getValue(occurrenceView.getInputElement()));
            });
            return values;
        }

    }
}