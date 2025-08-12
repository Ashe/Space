## What is Space?
Space is an open-source forum that I was making in order to learn Clojure. I've
never done any sort of web development other than this Blog with Hakyll and some
basic assignments for university. 

The idea of Space came from my experiences with learning and asking questions.
Sometimes, you just need to ask for help - maybe you don't know enough about the
subject to ask the right questions, or maybe the bug you're experiencing isn't
the code but your approach, something which is harder to learn in tutorials
that show solutions rather than workflow.

![Forum](/img/forum.png)

When newcomers go to [Stack Overflow](https://stackoverflow.com/), their first
impressions can be hit or miss. If they fail to supply a project that allows for
the reproduction of their error, they may get shot down before anyone offers to
help. This is very important, as Stack Overflow is more of a resource than a
support community, and so must maintain high-quality questions and answers to
hopefully answer the further questions on the same topic in the future.

However, not all questions are about correctness - when learning, a lot of
questions are merely probes for learning something new. If you're with a
teacher, asking good questions is a great way to learn, but the requirement of
preparing code snippets makes this rather difficult.

I intended Space to be a compliment of Stack Overflow. I want it to be a forum
that a community could set up and open up to newcomers, and to encourage people
to offer their advice and ideas whenever possible through gamification. With
Space being open source, a community would host their own Space and have true
ownership of their content. While [Reddit](https://reddit.com) is a great place,
it did kill off most forums by having everything in one place. Because of this,
a lot of communities suffer from having to do things the Reddit way, as well as
their identities being shared between each community they're a part of.

![Create a post](/img/create.png)

## Gamification for Space
I have this idea of tags being like skill levels that you can work on while
using Space. They are both the ranking system of posts as they act like a bounty
value, and the incentive for people to respond to posts made by other users.
It's a cycle.

The goal of a tag is to act as a level. When you make a post, you associate tags
with it that will act as categories. The post will have levels associated with
its tags that come from your own levels - meaning that in a programming Space, a
user's `Clojure` level will dictate the `Clojure` level of their post, assuming
they tagged it correctly.

![User](/img/user.png)

Posts accumilate experience points through a variety of factors, but ultimately
the longer the post stays up and the attention it receives increases its levels
further. The higher level the post, the more experience points users will
receive from participating and responding to said post. Thus, you earn points by
solving people's problems or discussing their topics, and when you decide to
post, your post will be considered more worthwhile for other users. 

This doesn't mean that newcomers posts get placed at the back of the queue - 
instead, it means that their posts become easy targets for experience points as 
if their posts go unnoticed for too long, the experience rewards will increase 
and thus it becomes more eyecatching for people to look at. It also would mean 
that users will have a higher reward for participating in low-difficulty threads.

Tags could eventually evolve elsewhere too. They could be used as currency, used
in forum games or give aways or used as roles to organise site users further.

![Create](/img/discussion.png)

## Current State
Space is currently in its early stages. You can create posts (as a guest,
anonymous or as a plain old user), log in admins can see through anonymous
posts. Posts are written in Markdown and tags can entered using a custom widget
I created.

![Sign in](/img/signin.png)

## Contribution
I haven't written much documentation and so contribution may be difficult right
now. I'm mainly open sourcing it now so that people can ask questions or have a
peek, but I accept that it's going to be a solo effort until the site becomes
fully functional.

