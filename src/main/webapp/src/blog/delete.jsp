<%@ page pageEncoding="UTF-8" %> <%@include file="../../head.jsp" %> <%@include
file="../../header.jsp" %>

<div class="main flex flex-col items-center justify-center grow gap-2">
  <form
    name="deleteBlog"
    method="post"
    action="/blog"
    class="flex flex-col gap-2 w-1/3"
    id="deleteBlog"
  >
    <h1 class="text-2xl text-center">Supprimer ce piggy</h1>

    <div class="flex flex-row gap-2">
      <a href="/blog/${blog.id}" class="bg-green-700 rounded-lg px-4 py-2 w-fit"
        >Nonnnnnn, je me suis trompé</a
      >
      <input
        type="submit"
        value="Supprimer mon piggy :("
        class="bg-red-700 rounded-lg px-4 py-2 w-fit"
      />
    </div>
    <input
      type="hidden"
      id="id"
      name="id"
      value="${blog.id}"
      class="rounded-lg p-2 text-black"
    />
    <input type="hidden" name="_method" value="DELETE" />
  </form>
</div>
