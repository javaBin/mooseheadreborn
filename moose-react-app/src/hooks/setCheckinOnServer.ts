import {EntryRegistrationForWorkshopType, UpdateCheckinInputType} from "../ServerTypes";
import ServerConfig from "../ServerConfig";

const setCheckinOnServer = (updateCheckinInputType:UpdateCheckinInputType):Promise<EntryRegistrationForWorkshopType> => {
    return new Promise((resolve, reject) => {

        fetch(ServerConfig.address + "/api/admin/updateCheckin", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(updateCheckinInputType)
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    return response.text().then(errorText => {
                        throw new Error(errorText);
                    });

                }
            })
            .then(json => {
                resolve(json);
            })
            .catch(error => {
                reject(error.message);
            });
    })

};

export default setCheckinOnServer;