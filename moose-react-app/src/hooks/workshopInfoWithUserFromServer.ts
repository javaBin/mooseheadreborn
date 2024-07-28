import {WorkshopInfoFromServer} from "../ServerTypes";
import ServerConfig from "../ServerConfig";


const workshopInfoWithUserFromServer = (workshopId:string,accessToken:string|null):Promise<WorkshopInfoFromServer> => {
    return new Promise((resolve, reject) => {
        const readWorkshopInput = {
            workshopId: workshopId,
            accessToken: accessToken
        };
        fetch(ServerConfig.address + "/api/readWorkshop", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(readWorkshopInput)
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
}

export default workshopInfoWithUserFromServer