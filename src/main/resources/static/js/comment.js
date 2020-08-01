
async function commentFormHandler(event) {
  event.preventDefault();

  const commentText = document.querySelector('textarea[name="comment-body"]').value.trim();

  const postId = window.location.toString().split('/')[
    window.location.toString().split('/').length - 1
  ];

  if (commentText) {
    const response = await fetch('/comments', {
      method: 'POST',
      body: JSON.stringify({
        postId,
        commentText
      }),
      headers: {
        'Content-Type': 'application/json'
      }
    });

    if (response.ok) {
      document.location.reload();
    } else {
      alert(response.statusText);
    }
  }

}

document.querySelector('.comment-form').addEventListener('submit', commentFormHandler);