module API_content_data{

    export class Data {

        private name:string;

        private arrayIndex:number;

        private parent:DataSet;

        constructor(name:string) {
            this.name = name;
        }

        setArrayIndex(value:number) {
            this.arrayIndex = value;
        }

        public setParent(parent:DataSet) {
            this.parent = parent;
        }

        getName():string {
            return this.name;
        }

        getParent():Data {
            return this.parent;
        }

        getArrayIndex():number {
            return this.arrayIndex;
        }
    }

}