<#import "/spring.ftl" as spring/>
<#import "container.ftlh" as c>
<@c.container>
    <div class="card">
        <h5 class="card-header"><@spring.message "viewacl.title"/> : ${acl.name}</h5>
        <div class="card-body">
            <div class="form-group row">
                <label for="name"
                       class="col-sm-2 col-form-label font-weight"><@spring.message "viewacl.label.name"/></label>
                <div class="col-sm-4">
                    <input type="text" readonly class="form-control" id="name" value="${acl.name}">
                </div>
            </div>
            <div class="form-group row">
                <label for="description"
                       class="col-sm-2 col-form-label font-weight"><@spring.message "viewacl.label.description"/></label>
                <div class="col-sm-4">
                    <input type="text" readonly class="form-control" id="description" value="${acl.description}">
                </div>
            </div>
            <div class="form-group row">
                <label for="objtypes"
                       class="col-sm-2 col-form-label font-weight"><@spring.message "viewacl.label.objtypes"/></label>
                <div class="col-sm-4">
                    <select multiple readonly class="form-control" id="objtypes">
                        <#list acl.objTypes as objType>
                            <option>${objType}</option></#list>
                    </select>
                </div>
            </div>
            <div class="form-group row">
                <label for="statuses"
                       class="col-sm-2 col-form-label font-weight"><@spring.message "viewacl.label.statuses"/></label>
                <div class="col-sm-4">
                    <select multiple readonly class="form-control" id="statuses">
                        <#list acl.statuses as status>
                            <option>${status}</option></#list>
                    </select>
                </div>
            </div>
        </div>
    </div>
    <div class="d-flex">
        <div class="p-2"><input class="btn btn-primary mr-2" name="ok"
                                value="<@spring.message "viewacl.button.ok"/>"
                                title="<@spring.message "viewacl.button.ok"/>" type="button"
                                onClick="location.href='/acls'"/></div>
        <div class="p-2"><input class="btn btn-primary mr-2" name="edit"
                                value="<@spring.message "viewacl.button.edit"/>"
                                title="<@spring.message "viewacl.button.edit"/>" type="button"
                                onClick="location.href='/acls/${acl.id}/edit'"/></div>
    </div>
</@c.container>
