module app_contextwindow {
    export interface Component {
        name:string;
        key:number;
        componentType:{
            cssSelector:string;
            cursor:string;
            type:string;
            typeName:string;
            iconCls:string;
        };
    }
}