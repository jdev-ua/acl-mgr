<#import "/spring.ftl" as spring/>
<#import "container.ftlh" as c>
<@c.container>
    <script type="text/javascript">
        function onDelete(id) {
            // change current HTTP method on DELETE
            document.getElementById("_method").value = "delete";
            let form = document.getElementById("editACL");
            // actualize form action
            form.action = form.action + "/" + id;
            form.submit();
        }
    </script>
    <form id="editACL" action="/acls" method="post" modelAttribute="acl">
        <input type="hidden" id="_method" name="_method" value="patch"/>
        <div class="card">
            <h5 class="card-header"><@spring.message "title.editacl"/> : ${acl.name}</h5>
            <div class="card-body">
                <div class="form-group row">
                    <label for="name" class="col-sm-2 col-form-label"><@spring.message "label.name"/></label>
                    <div class="col-sm-4">
                        <@spring.bind "acl.name"/>
                        <input class="form-control ${(spring.status.errorMessages?has_content)?string("is-invalid","")}"
                               type="text" name="${spring.status.expression}" value="${spring.status.value}"
                               placeholder="Enter name"/>
                        <#list spring.status.errorMessages as error>
                            <div class="invalid-feedback">${error}</div>
                        </#list>
                    </div>
                </div>
                <div class="form-group row">
                    <label for="description"
                           class="col-sm-2 col-form-label"><@spring.message "label.description"/></label>
                    <div class="col-sm-4">
                        <@spring.bind "acl.description"/>
                        <input class="form-control ${(spring.status.errorMessages?has_content)?string("is-invalid","")}"
                               type="text" name="${spring.status.expression}" value="${spring.status.value}"/>
                        <@spring.showErrors "<br>" "invalid-feedback"/>
                    </div>
                </div>
                <div class="form-group row">
                    <label for="objTypes"
                           class="col-sm-2 col-form-label font-weight"><@spring.message "label.objtypes"/></label>
                    <div class="col-sm-4">
                        <@spring.formMultiSelect "acl.objTypes" dictObjTypes 'class="form-control" id="objTypes"'/>
                    </div>
                </div>
                <div class="form-group row">
                    <label for="statuses"
                           class="col-sm-2 col-form-label font-weight"><@spring.message "label.statuses"/></label>
                    <div class="col-sm-4">
                        <@spring.formMultiSelect "acl.statuses" dictStatuses 'class="form-control"'/>
                    </div>
                </div>
            </div>
        </div>
        <div class="d-flex">
            <div class="p-2">
                <input class="btn btn-primary mr-2" name="delete"
                       value="<@spring.message "button.delete"/>" type="button"
                       title="<@spring.message "button.delete"/>" onClick="onDelete('${acl.id}')"/>
            </div>
            <div class="p-2">
                <input class="btn btn-primary mr-2" name="apply"
                       value="<@spring.message "button.apply"/>"
                       title="<@spring.message "button.apply"/>" type="submit"/>
            </div>
            <div class="p-2">
                <input class="btn btn-primary mr-2" name="cancel"
                       value="<@spring.message "button.cancel"/>"
                       title="<@spring.message "button.cancel"/>" type="button"
                       onClick="location.href='/acls'"/>
            </div>
        </div>
    </form>
</@c.container>
