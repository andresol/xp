module api.form.inputtype.combobox {

    export interface ComboBoxConfig {
        options: ComboBoxOption[]
    }

    export class ComboBox extends api.form.inputtype.support.BaseInputTypeManagingAdd<string> {

        private context: api.form.inputtype.InputTypeViewContext<ComboBoxConfig>;

        private comboBoxConfig: ComboBoxConfig;

        private input: api.form.Input;

        private comboBox: api.ui.selector.combobox.ComboBox<string>;

        private selectedOptionsView: api.ui.selector.combobox.SelectedOptionsView<string>;

        private previousValidationRecording: api.form.inputtype.InputValidationRecording;

        constructor(context: api.form.inputtype.InputTypeViewContext<ComboBoxConfig>) {
            super("combo-box");
            this.addClass("input-type-view");
            this.context = context;
            this.comboBoxConfig = context.inputConfig;
        }

        availableSizeChanged() {
            console.log("ComboBox.availableSizeChanged(" + this.getEl().getWidth() + "x" + this.getEl().getWidth() + ")");
        }

        getValueType(): api.data.type.ValueType {
            return api.data.type.ValueTypes.STRING;
        }

        newInitialValue(): string {
            return null;
        }

        layout(input: api.form.Input, properties: api.data.Property[]) {

            this.input = input;

            this.selectedOptionsView = new api.ui.selector.combobox.BaseSelectedOptionsView<string>();
            this.comboBox = this.createComboBox(input);

            this.comboBoxConfig.options.forEach((option: ComboBoxOption) => {
                this.comboBox.addOption({value: option.value, displayValue: option.label})
            });

            var valueArray: string[] = [];
            properties.forEach((property: api.data.Property) => {
                valueArray.push(property.getString());
            });
            this.comboBox.setValues(valueArray);


            this.appendChild(this.comboBox);
            this.appendChild(this.selectedOptionsView);
        }

        createComboBox(input: api.form.Input): api.ui.selector.combobox.ComboBox<string> {
            var comboBox = new api.ui.selector.combobox.ComboBox<string>(name, {
                filter: this.comboboxFilter,
                selectedOptionsView: this.selectedOptionsView,
                maximumOccurrences: input.getOccurrences().getMaximum(),
                optionDisplayValueViewer: new ComboBoxDisplayValueViewer(),
                hideComboBoxWhenMaxReached: true
            });

            comboBox.onOptionFilterInputValueChanged((event: api.ui.selector.OptionFilterInputValueChangedEvent<string>) => {
                this.comboBox.setFilterArgs({searchString: event.getNewValue()});
            });
            comboBox.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<string>) => {

                var value = new api.data.Value(event.getOption().value, api.data.type.ValueTypes.STRING);

                if (comboBox.countSelectedOptions() == 1) { // overwrite initial value
                    this.notifyValueChanged(new api.form.inputtype.ValueChangedEvent(value, 0));
                }
                else {
                    this.notifyValueAdded(value);
                }


                this.validate(false);
            });
            comboBox.onOptionDeselected((removed: api.ui.selector.combobox.SelectedOption<string>) => {

                this.notifyValueRemoved(removed.getIndex());

                this.validate(false);
            });

            return comboBox;
        }

        getValues(): api.data.Value[] {

            var values: api.data.Value[] = [];
            this.comboBox.getSelectedOptions().forEach((option: api.ui.selector.Option<string>)  => {
                var value = new api.data.Value(option.value, api.data.type.ValueTypes.STRING);
                values.push(value);
            });
            return values;
        }

        giveFocus(): boolean {
            if (this.comboBox.maximumOccurrencesReached()) {
                return false;
            }
            return this.comboBox.giveFocus();
        }

        valueBreaksRequiredContract(value: api.data.Value): boolean {
            return value.isNull() || !value.getType().equals(api.data.type.ValueTypes.STRING) || !this.isExistingValue(value.asString());
        }

        private isExistingValue(value: string): boolean {
            return this.comboBoxConfig.options.some((option: ComboBoxOption) => {
                return option.value == value;
            });
        }

        private comboboxFilter(item: api.ui.selector.Option<string>, args) {
            return !(args && args.searchString && item.displayValue.toUpperCase().indexOf(args.searchString.toUpperCase()) == -1);
        }

        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {

            var recording = new api.form.inputtype.InputValidationRecording();

            var numberOfValids = this.comboBox.countSelectedOptions();
            if (numberOfValids < this.input.getOccurrences().getMinimum()) {
                recording.setBreaksMinimumOccurrences(true);
            }
            if (this.input.getOccurrences().maximumBreached(numberOfValids)) {
                recording.setBreaksMaximumOccurrences(true);
            }

            if (!silent && recording.validityChanged(this.previousValidationRecording)) {
                this.notifyValidityChanged(new api.form.inputtype.InputValidityChangedEvent(recording, this.input.getName()));
            }

            this.previousValidationRecording = recording;
            return recording;
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.comboBox.onFocus(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.comboBox.unFocus(listener);
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.comboBox.onBlur(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.comboBox.unBlur(listener);
        }

    }

    api.form.inputtype.InputTypeManager.register(new api.Class("ComboBox", ComboBox));
}