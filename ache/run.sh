#set JAVA_HOME
export JAVA_HOME=/home/vgc/rajat_pawar/JDK/jdk1.6.0_35/
#compile 
./script/compile_crawler.sh
#run
./script/start_crawler.sh rajat_crawler ./config/sample_config/ ./config/sample.seeds ./config/sample_model ./rajat_run