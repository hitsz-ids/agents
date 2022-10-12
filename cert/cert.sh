# 注：.key 可以看作私钥，.pem 可以看作公钥

# genrsa 命令生成一个 2048 bit 的私钥，输出到 ca.key 文件中
# req 命令通过 ca.key 生成一个自签名证书，输出到 pem 文件中
mkdir output
cd output
mkdir root
mkdir client
mkdir server
cd root
rm -rf *
# 根证书公私钥
openssl genrsa -out ca.key 2048
openssl req -new -x509 -key ca.key -out ca.pem << EOF
CN
China
hitsz_ids
org.hitsz_ids
org.hitsz_ids
agents
chuanlijian@gmail.com


EOF
# 客户端公私钥
cd ..
cd client
rm -rf *
openssl genrsa -out client.key 2048
openssl req -new -key client.key -out client.csr << EOF
CN
China
hitsz_ids
org.hitsz_ids
org.hitsz_ids
agents
chuanlijian@gmail.com


EOF
openssl x509 -req -sha256 -CA ../root/ca.pem -CAkey ../root/ca.key -CAcreateserial -days 3650 -in client.csr -out client.pem
openssl pkcs8 -topk8 -nocrypt -in client.key -out client-pkcs8.key
cd ..
cd root
rm -rf ca.srl

# 服务端公私钥
cd ..
cd server
rm -rf *
openssl genrsa -out server.key 2048
openssl req -new -key server.key -out server.csr << EOF
CN
China
hitsz_ids
org.hitsz_ids
org.hitsz_ids
agents
chuanlijian@gmail.com


EOF

openssl x509 -req -sha256 -CA ../root/ca.pem -CAkey ../root/ca.key -CAcreateserial -days 3650 -in server.csr -out server.pem
openssl pkcs8 -topk8 -nocrypt -in server.key -out server-pkcs8.key
cd ..
cd root
rm -rf ca.srl