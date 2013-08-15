module api_remote_content {

    export interface Data {
        name: string;
        path: string;
        type: string;
        value;
    }

    export interface DataSet extends Data {
        value: Data[];
    }

    export interface ContentDataProperty  extends Data{
        value: string;
    }

    export interface ContentSummary {
        id: string;
        path: string;
        name: string;
        type: string;
        displayName: string;
        owner: string;
        modifier: string;
        modifiedTime: Date;
        createdTime: Date;
        iconUrl: string;
    }

    export interface ContentGet extends ContentSummary{
        isRoot: bool;
        data: Data[];
    }

    export interface ContentList extends ContentSummary{
        editable: bool;
        deletable: bool;
        allowsChildren: bool;
    }

    export interface ContentFind extends ContentList{
        order: number;
        score: number;
    }

    export interface ContentFacet {
        name: string;
        displayName: string;
        _type: string;
        terms?: {
            name?: string;
            displayName?: string;
            count?: number;
            time?: number;
        }[];
        ranges?: {
            from: string;
            to: string;
            total_count: number;
        }[];
    }

    export interface ContentTreeNode {
        allowsChildren:bool;
        contents:ContentTreeNode[];
        createdTime?:Date;
        deletable:bool;
        displayName:string;
        editable:bool;
        hasChildren:bool;
        iconUrl:string;
        id:string;
        modifiedTime?:Date;
        modifier:string;
        name:string;
        owner:string;
        path:string;
        type:string;
    }

    export interface GetParams {
        path?: string;
        contentIds?: string[];
    }

    export interface GetResult {
        content: ContentGet[];
    }

    export interface ListParams {
        path: string;
    }

    export interface ListResult {
        total: number;
        contents: ContentList[];
    }

    export interface FindParams {
        fulltext?: string;
        includeFacets?: bool;
        contentTypes: string[];
        spaces?: string[];
        ranges?: {
            lower: string;
            upper: string;
        }[];
        facets: {
            [key:string]:any;
        };
    }

    export interface FindResult {
        total: number;
        contents: ContentFind[];
        facets?: ContentFacet[];
    }

    export interface ValidateParams {
        qualifiedContentTypeName: string;
        contentData: Data;
    }

    export interface ValidateResult {
        hasError: bool;
        errors: {
            path: string;
            message: string;
        }[];
    }

    export interface DeleteParams {
        contentPaths: string[];
    }

    export interface DeleteResult {
        successes: {
            path:string;
        }[];
        failures: {
            path:string;
            reason:string;
        }[];
    }

    export interface CreateOrUpdateParams {
        contentId?: string;
        temporary?: bool;
        contentName?: string;
        parentContentPath?: string;
        qualifiedContentTypeName: string;
        contentData: {
            [key:string]: string;
        };
        displayName: string;
        attachments?: {
            uploadId: string;
            attachmentName: string;
        }[];
    }

    export interface CreateOrUpdateResult{
        created: bool;
        updated: bool;
        contentId?: string;
        contentPath?: string;
        failure?: string;
    }

    export interface GetTreeParams {
        contentIds?:string[];
    }

    export interface GetTreeResult {
        total:number;
        contents:ContentTreeNode[];
    }

}