node{
    //环境变量
    def REPOSITORY = 'REPOSITORY'
    def PROJECT ='PROJECT'
    def IMAGEVERSION = 'latest'
    //更新代码
    //登录docker私有镜像仓库
    stage('login docker respository'){
        sh "docker login ${REPOSITORY} -u USERNAME -p PASSWORD"
    }
    
    //制作docker镜像并上传仓库
    stage('make and push docker image'){
        sh """
            [ -d /tmp/tag/${JOB_NAME} ] && rm -rf /tmp/tag/${JOB_NAME}
            mkdir /tmp/tag/${JOB_NAME} -p
            
            WORKDIR=`pwd`
            
            DEST=`find . -name Dockerfile | awk -F'src' '{print \$1}'`
            
            func(){
                cd \${WORKDIR}
                cd \${dst}/target
                IMAGENAME=`echo \${dst} | awk -F'/' '{print \$(NF-1)}'`
                echo '0' > /tmp/tag/${JOB_NAME}/\${IMAGENAME}
                
                docker build -t ${REPOSITORY}/${PROJECT}/\${IMAGENAME}:${IMAGEVERSION} -f ../src/Dockerfile .
                docker push ${REPOSITORY}/${PROJECT}/\${IMAGENAME}:${IMAGEVERSION}
                
                echo '1' > /tmp/tag/${JOB_NAME}/\${IMAGENAME}
            }
            
            for dst in \${DEST}
            do
                func &
            done
        """
    }
        stage("restart pod"){
        sh """
            while true
            do
                grep -r '0' /tmp/tag/${JOB_NAME} || exit 0
                sleep 3
            done
        """
    }
}
