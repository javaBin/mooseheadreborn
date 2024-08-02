import {AddParticipantInput} from "../ServerTypes";
import ServerConfig from "../ServerConfig";

const registerParticipantToserver = (addParticipantInput:AddParticipantInput):Promise<string|null> => {
    return new Promise((resolve, reject) => {
        fetch(ServerConfig.address + "/api/registerParticipant", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(addParticipantInput)
        })
            .then(response => {
                if (response.ok) {
                    resolve(null);
                } else {
                    return response.text().then(errorText => {
                        throw new Error(errorText);
                    });

                }
            })
            //  .then(errormessage => resolve(errormessage))
            .catch(error => {
                resolve(error.message);
            });
    });
}

export default registerParticipantToserver;
