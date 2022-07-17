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
        <input type="hidden" id="_method" name="_method" value="<#if httpMethod??>${acl.name}<#else>patch</#if>"/>
        <div class="card">
            <h5 class="card-header"><@spring.message "title.editacl"/> : ${acl.name}</h5>
            <div class="card-body">
                <ul class="nav nav-tabs" id="aclTab" role="tablist">
                    <li class="nav-item">
                        <a class="nav-link active" id="info-tab" data-toggle="tab" href="#info" role="tab"
                           aria-controls="info" aria-selected="true"><@spring.message "tab.info"/></a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" id="accessors-tab" data-toggle="tab" href="#accessors" role="tab"
                           aria-controls="accessors" aria-selected="false"><@spring.message "tab.accessors"/></a>
                    </li>
                </ul>
                <div class="tab-content" id="aclTabContent">
                    <div class="tab-pane fade show active" id="info" role="tabpanel" aria-labelledby="info-tab">
                        <div class="form-group row"></div>
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
                    <div class="tab-pane fade" id="accessors" role="tabpanel" aria-labelledby="accessors-tab">
                        <#if acl.accessors?has_content>
                            <div style="overflow: auto; height:400px">
                                <table class="table table-striped">
                                    <thead>
                                    <tr>
                                        <th scope="col">ID</th>
                                        <th scope="col"><@spring.message "label.name"/></th>
                                        <th scope="col"><@spring.message "label.permit"/></th>
                                        <th scope="col"><@spring.message "label.alias"/></th>
                                        <th scope="col"><@spring.message "label.svc"/></th>
                                        <th scope="col"><@spring.message "label.orglevels"/></th>
                                        <th scope="col"><@spring.message "label.xpermits"/></th>
                                        <th scope="col"></th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <#list acl.accessors as accessor>
                                        <tr>
                                            <td><#if accessor.id??>${accessor.id}<#else>null</#if></td>
                                            <td>${accessor.name}</td>
                                            <td>${accessor.permit}</td>
                                            <td>
                                                <#if accessor.alias>
                                                    <span class="fas fa-check" style="color: green" title="Yes"></span>
                                                <#else>
                                                    <span class="fas fa-times" style="color: red" title="No"></span>
                                                </#if>
                                            </td>
                                            <td>
                                                <#if accessor.svc>
                                                    <span class="fas fa-check" style="color: green" title="Yes"></span>
                                                <#else>
                                                    <span class="fas fa-times" style="color: red" title="No"></span>
                                                </#if>
                                            </td>
                                            <td>
                                                <#list accessor.orgLevels as orgLevel>
                                                    ${orgLevel}<#if orgLevel_has_next>,</#if>
                                                </#list>
                                            </td>
                                            <td>
                                                <#list accessor.XPermits as XPermit>
                                                    ${XPermit}<#if XPermit_has_next><br></#if>
                                                </#list>
                                            </td>
                                            <td>
                                                <button class="btn btn-outline-primary btn-sm" type="button"
                                                        title="<@spring.message "tooltip.edit"/>"
                                                        onClick="location.href='/acls/${acl.id}/edit?accessorName=${accessor.name}'">
                                                    <nobr>
                                                        <span class="fas fa-edit"></span>&nbsp;<@spring.message "button.edit"/>
                                                    </nobr>
                                                </button>
                                            </td>
                                        </tr>
                                    </#list>
                                    </tbody>
                                </table>
                            </div>
                        <#else>
                            <span><@spring.message "list.empty"/></span>
                        </#if>
                        <div class="form-group row">
                            <div class="col-sm-10">
                                <button class="btn btn-outline-primary btn-sm" type="button"
                                        title="<@spring.message "tooltip.newaccessor"/>"
                                        onClick="location.href='/acls/${acl.id}/edit?addAccessor=true'">
                                    <nobr>
                                        <span class="fas fa-plus"></span>&nbsp;<@spring.message "button.add"/>
                                    </nobr>
                                </button>
                            </div>
                        </div>
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
