#!/usr/bin/env bash

# ISEL/DEETC/MEIC/IESD 2022
# Installation and configuration of a web application
apt update
apt install unzip
curl -fsSL https://deno.land/x/install/install.sh | sh
echo 'export DENO_INSTALL="/home/vagrant/.deno"
export PATH="$DENO_INSTALL/bin:$PATH"
  ' > .bash_profile
chown vagrant .bash_profile
cp -r /root/.deno .
chown -R vagrant .deno
# snap install deno
# apt install -y default-jdk

echo 'import { serve } from "https://deno.land/std@0.119.0/http/server.ts";
function handler(_req: Request): Response {
  const body = new TextEncoder().encode("<h1>IESD 2122 - Hello, World!</h1>\n");
  return new Response(body);
}
console.log("Listening on http://localhost:8000");
serve(handler);' > hello-deno.ts
chown vagrant hello-deno.ts
echo 'Before calling deno...'
/home/vagrant/.deno/bin/deno --version
/home/vagrant/.deno/bin/deno run --allow-net hello-deno.ts &> /dev/null &
# To start the web application even when the operating system is rebooted
echo '/home/vagrant/.deno/bin/deno run --allow-net hello-deno.ts &> /dev/null &' >> .bash_profile
echo 'After calling deno...'
# Install net-tools, e.g., make available the shell ifconfig command
# apt install net-tools
