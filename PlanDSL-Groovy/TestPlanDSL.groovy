project(key: '8AUB2', name: '8AuBranch2') {
    plan(key: 'MAIN8', name: 'Main8') {
        description 'Main build from trunk'
        enabled false

        scm {
        }

        stage(name: 'Default Stage') {
            description ''
            manual false

            job(key: 'JOB1', name: 'Default Job') {
                description ''
                enabled true

                tasks {
                    custom(pluginKey: 'com.atlassian.bamboo.plugins.vcs:task.vcs.checkout') {
                        description 'Checkout Default Repository'
                        enabled true
                        isFinal false
                        configure(
                                'cleanCheckout': 'false',
                                'selectedRepository_0': 'defaultRepository',
                                'checkoutDir_0': '',
                        )
                    }
                    custom(pluginKey: 'com.atlassian.bamboo.plugins.scripttask:task.builder.script') {
                        description 'Run script'
                        enabled true
                        isFinal false
                        configure(
                                'interpreter': 'RUN_AS_EXECUTABLE',
                                'scriptLocation': 'INLINE',
                                'scriptBody': './skript.sh',
                        )
                    }
                }

                artifacts {
                    definition(name: 'db artifacts', copyPattern: 'db/**') {
                        location ''
                        shared true
                    }

                }
            }
        }

        deploymentProject(name: 'AuBranch2') {
            description ''

            environment(name: 'QA-release') {
                description ''

                tasks {
                    custom(pluginKey: 'com.atlassian.bamboo.plugins.bamboo-artifact-downloader-plugin:cleanWorkingDirectoryTask') {
                        description ''
                        enabled true
                        isFinal false
                        configure(
                        )
                    }
                    custom(pluginKey: 'com.atlassian.bamboo.plugins.bamboo-artifact-downloader-plugin:artifactdownloadertask') {
                        description 'Download release contents'
                        enabled true
                        isFinal false
                        configure(
                                'sourcePlanKey': 'AUB2-MAIN',
                                'artifactId_0': '-1',
                                'localPath_0': '',
                        )
                    }
                    custom(pluginKey: 'com.atlassian.bamboo.plugins.scripttask:task.builder.script') {
                        description 'run deploy'
                        enabled true
                        isFinal false
                        configure(
                                'interpreter': 'RUN_AS_EXECUTABLE',
                                'scriptLocation': 'INLINE',
                                'scriptBody': 'sudo deploy.sh',
                        )
                    }
                }
            }
            environment(name: 'okint-db') {
                description ''

                tasks {
                    custom(pluginKey: 'com.atlassian.bamboo.plugins.scripttask:task.builder.script') {
                        description 'checkout db repo'
                        enabled true
                        isFinal false
                        configure(
                                'interpreter': 'POWERSHELL',
                                'scriptLocation': 'INLINE',
                                'scriptBody': '''Remove-Item -Recurse -Force deploy/
                    mkdir deploy
                    git clone -b "main" --depth=1 "http://" deploy/
                    ''',
                        )
                    }
                    custom(pluginKey: 'com.atlassian.bamboo.plugins.scripttask:task.builder.script') {
                        description 'upgrade DB pomoci liquibase'
                        enabled true
                        isFinal false
                        configure(
                                'interpreter': 'POWERSHELL',
                                'scriptLocation': 'INLINE',
                                'scriptBody': '''$env:Path += ";$env:bamboo_capability_dir_liquibase_4_7_1"
                    $env:NLS_LANG="AMERICAN_CZECH REPUBLIC.AL32UTF8"
                    ./liquibase_update.sh
                     if ($?) { exit 0 } else { exit -1 }
                    ''',
                                'workingSubDirectory': 'upgrade/',
                        )
                    }
                }
            }
        }
    }
}