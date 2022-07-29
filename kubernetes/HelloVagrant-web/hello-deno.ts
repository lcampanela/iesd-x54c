import { serve } from "https://deno.land/std@0.119.0/http/server.ts";
function handler(_req: Request): Response {
  const body = new TextEncoder().encode("<h1>IESD 2122 - Hello, World!</h1>\n");
  return new Response(body);
}
console.log("Listening on http://localhost:8000");
serve(handler);
