node{
    stage("docker build and docker push"){
        sh """
            [ -d /tmp/tag/${JOB_NAME} ] && rm -rf /tmp/tag/${JOB_NAME}
            mkdir /tmp/tag/${JOB_NAME} -p

            build(){
                echo '0' > /tmp/tag/${JOB_NAME}/\${service}

                //在这里完成docker build和docker push

                echo '1' > /tmp/tag/${JOB_NAME}/\${service}
            }

            for service in services
            do
                build &    //将一个docker的build push放到一个build函数中去完成
            done
        """
    }

    stage("restart pod"){
        sh """
            while true
            do
                grep -r '0' /tmp/tag/${JOB_NAME} || //在这里完成pod的重启
                sleep 3
            done
        """
    }
}