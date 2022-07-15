<#import "container.ftlh" as c>
<#import "/spring.ftl" as spring/>
<@c.container>
    <div class="card w-75">
        <h5 class="card-header"><@spring.message "title.acls"/></h5>
        <div class="card-body">
            <div class="form-group row">
                <div class="col-sm-10">
                    <#if acls?has_content>
                        <div style="overflow: auto; width:800px; height:320px;border-style: solid; border-width: thin; border-color: lightgray">
                            <table class="table table-striped">
                                <thead>
                                <tr>
                                    <th scope="col">ID</th>
                                    <th scope="col"><@spring.message "label.name"/></th>
                                    <th scope="col"><@spring.message "label.description"/></th>
                                    <th scope="col"></th>
                                </tr>
                                </thead>
                                <tbody>
                                <#list acls as acl>
                                    <tr>
                                        <td>${acl.id}</td>
                                        <td>${acl.name}</td>
                                        <td>${acl.description}</td>
                                        <td>
                                            <button class="btn btn-link" type="button"
                                                    title="<@spring.message "button.details"/>"
                                                    onClick="location.href='/acls/${acl.id}'">
                                                <nobr><@spring.message "button.details"/></nobr>
                                            </button>
                                        </td>
                                    </tr>
                                </#list>
                                </tbody>
                            </table>
                        </div>
                    <#else>
                        <p><@spring.message "acls.list.empty"/></p>
                    </#if>
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-10">
                    <input class="btn btn-primary mr-2" name="add" value="<@spring.message "button.newacl"/>"
                           title="<@spring.message "button.newacl"/>" type="button"
                           onClick="location.href='/acls/new'"/>
                </div>
            </div>
        </div>
    </div>
</@c.container>
