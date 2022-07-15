<#import "/spring.ftl" as spring/>
<#import "container.ftlh" as c>
<@c.container>
    <form action="/acls" method="post" modelAttribute="acl">
        <div class="card">
            <h5 class="card-header"><@spring.message "title.newacl"/> : ${acl.name}</h5>
            <div class="card-body">
                <div class="form-group row">
                    <label for="name" class="col-sm-2 col-form-label"><@spring.message "label.name"/></label>
                    <div class="col-sm-4">
                        <@spring.bind "acl.name"/>
                        <input class="form-control ${(spring.status.errorMessages?has_content)?string("is-invalid","")}"
                               type="text" name="${spring.status.expression}" value="${spring.status.value}"
                               placeholder="<@spring.message "placeholder.name"/>"/>
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
                               type="text" name="${spring.status.expression}" value="${spring.status.value}"
                               placeholder="<@spring.message "placeholder.description"/>"/>
                        <#list spring.status.errorMessages as error>
                            <div class="invalid-feedback">${error}</div>
                        </#list>
                    </div>
                </div>
                <div class="form-group row">
                    <label for="objTypes"
                           class="col-sm-2 col-form-label font-weight"><@spring.message "label.objtypes"/></label>
                    <div class="col-sm-4">
                        <select multiple class="form-control" id="objTypes" name="objTypes">
                            <#list dictObjTypes as key, value>
                                <option <#if acl.objTypes?seq_contains(key)>selected="selected"</#if>>${value}</option></#list>
                        </select>
                    </div>
                </div>
                <div class="form-group row">
                    <label for="statuses"
                           class="col-sm-2 col-form-label font-weight"><@spring.message "label.statuses"/></label>
                    <div class="col-sm-4">
                        <select multiple class="form-control" id="statuses" name="statuses">
                            <#list dictStatuses as key, value>
                                <option <#if acl.statuses?seq_contains(key)>selected="selected"</#if>>${value}</option></#list>
                        </select>
                    </div>
                </div>
            </div>
        </div>
        <div class="d-flex">
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
