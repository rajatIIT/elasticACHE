while true
do
  x=$(ps aux | grep ache | grep kienpham | wc -l) #kienpham should be changed to username of crawler's owner
                                                  #also, we should change the client so that there is no need to rerun it
  if [ $x -lt 7 ]
  then
    sh script/run_client.sh config/sample_config
    echo "rerun"
  fi
  sleep 5m
done
