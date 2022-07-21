<#import "/spring.ftl" as spring/>
<#import "container.ftlh" as c>
<@c.container>
    <div class="card">
        <h5 class="card-header"><@spring.message "title.viewacl"/> : ${acl.name}</h5>
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
                        <label for="id"
                               class="col-sm-2 col-form-label font-weight">ID</label>
                        <div class="col-sm-4">
                            <input type="text" readonly class="form-control-plaintext" id="id"
                                   value="<#if acl.id??>${acl.id?c}<#else>null</#if>">
                        </div>
                    </div>
                    <div class="form-group row">
                        <label for="name"
                               class="col-sm-2 col-form-label font-weight"><@spring.message "label.name"/></label>
                        <div class="col-sm-4">
                            <input type="text" readonly class="form-control" id="name" value="${acl.name}">
                        </div>
                    </div>
                    <div class="form-group row">
                        <label for="description"
                               class="col-sm-2 col-form-label font-weight"><@spring.message "label.description"/></label>
                        <div class="col-sm-4">
                            <input type="text" readonly class="form-control" id="description"
                                   value="${acl.description}">
                        </div>
                    </div>
                    <div class="form-group row">
                        <label for="objTypes"
                               class="col-sm-2 col-form-label font-weight"><@spring.message "label.objtypes"/></label>
                        <div class="col-sm-4">
                            <select multiple readonly class="form-control" id="objTypes">
                                <#list acl.objTypes as objType>
                                    <option>${objType}</option></#list>
                            </select>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label for="statuses"
                               class="col-sm-2 col-form-label font-weight"><@spring.message "label.statuses"/></label>
                        <div class="col-sm-4">
                            <select multiple readonly class="form-control" id="statuses">
                                <#list acl.statuses as status>
                                    <option>${status}</option></#list>
                            </select>
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
                                                    title="<@spring.message "tooltip.details"/>"
                                                    onClick="location.href='/acls/${acl.id?c}?accessorName=${accessor.name}'">
                                                <nobr>
                                                    <span class="fas fa-eye"></span>&nbsp;<@spring.message "button.view"/>
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
                </div>
            </div>
        </div>
    </div>
    <div class="d-flex">
        <div class="p-2"><input class="btn btn-primary mr-2" name="ok"
                                value="<@spring.message "button.ok"/>"
                                title="<@spring.message "button.ok"/>" type="button"
                                onClick="location.href='/acls'"/>
        </div>
        <div class="p-2"><input class="btn btn-primary mr-2" name="edit"
                                value="<@spring.message "button.edit"/>"
                                title="<@spring.message "button.edit"/>" type="button"
                                onClick="location.href='/acls/${acl.id?c}/edit'"/>
        </div>
    </div>
</@c.container>