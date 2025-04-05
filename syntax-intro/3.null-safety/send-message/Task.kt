fun sendMessageToClient(
    client: Client?,
    message: String?,
    mailer: Mailer,
) {
    val contact: Contact? = client?.personalInfo
    if (contact is Contact) {
        mailer.sendMessage(contact.email, if (message is String) message else "Hello!")
    }
}
