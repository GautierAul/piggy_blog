<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <script src="https://cdn.tailwindcss.com"></script>
  <link href="/styles/index.css" rel="stylesheet" />
  <link rel="icon" href="/images/piggy.jpeg" />

  <title>Piggy blog</title>
</head>
<div id="header" class="h-20 w-full header flex-none">
  <div class="flex flex-row h-full items-center justify-between px-4">
    <div class="flex flex-row gap-2 items-center">
      <img
        src="/images/piggy.jpeg"
        class="w-10 h-10 rounded-full hover:animate-spin"
        alt="logo piggy blog"
      />
      <a href="/" class="text-2xl">Piggy blog</a>
    </div>
    <div id="nav-block" class="flex flex-row gap-4">
      <a id="disconnect" class="cursor-pointer">Me deconnecter</a>
    </div>
  </div>
</div>

<script>
  if (sessionStorage.getItem("token") == null) {
    document.getElementById("nav-block").className = "invisible";
  } else {
    document.getElementById("nav-block").className = "flex flex-row gap-4 ";
  }

  document
    .getElementById("disconnect")
    .addEventListener("click", function (event) {
      sessionStorage.removeItem("token");
      sessionStorage.removeItem("userId");
      window.location.href = "/";
    });
</script>
